package com.app.noobshop.security.filter;

import com.app.noobshop.common.constant.JwtTokenClaimsConstant;
import com.app.noobshop.common.context.BaseContext;
import com.app.noobshop.common.result.UserInfo;
import com.app.noobshop.common.util.JwtUtils;
import com.app.noobshop.properties.JwtProperties;
import com.app.noobshop.security.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * JWT 认证过滤器 - 替代 Shiro 的 JwtFilter
 * 继承 OncePerRequestFilter 确保每个请求只执行一次
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtProperties jwtProperties,
                                   CustomUserDetailsService userDetailsService) {
        this.jwtProperties = jwtProperties;
        this.userDetailsService = userDetailsService;
    }

    public JwtAuthenticationFilter(JwtProperties jwtProperties) {
        this(jwtProperties, null);
    }

    // Optional 认证路径正则（评论区查看接口）
    private static final Pattern OPTIONAL_AUTH_PATTERN = Pattern.compile("/api/user/product/comment/.*/show");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                      HttpServletResponse response,
                                      FilterChain filterChain) throws ServletException, IOException {
        try {
            // 检查是否为 OPTIONS 请求（优先检查，避免不必要的 Header 解析）
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = getTokenFromRequest(request);
            String requestPath = request.getRequestURI();
            boolean isOptionalPath = OPTIONAL_AUTH_PATTERN.matcher(requestPath).matches();

            // 没有 Token 的情况
            if (StringUtils.isBlank(token)) {
                if (isOptionalPath) {
                    // 可选认证路径，无 Token 也放行
                    filterChain.doFilter(request, response);
                } else {
                    // 非可选路径，交给后续过滤器处理（会触发认证失败）
                    filterChain.doFilter(request, response);
                }
                return;
            }

            // 有 Token，进行验证
            try {
                Claims claims = JwtUtils.parseJWT(jwtProperties.getUserSecretKey(), token);
                String userId = claims.get(JwtTokenClaimsConstant.SYS_USER_ID).toString();

                // 权限保存在服务端缓存中，角色变更后删除缓存即可立即生效。
                UserDetails userDetails = userDetailsService == null
                        ? new User(userId, "", List.of())
                        : userDetailsService.loadUserByUsername(userId);

                // 构建认证对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                token,
                                userDetails.getAuthorities()
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 设置安全上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 设置 ThreadLocal 用户信息（兼容原有代码）
                BaseContext.setUserInfo(buildUserInfo(userId, claims));

                log.debug("JWT 认证成功，userId: {}", userId);

            } catch (ExpiredJwtException e) {
                log.warn("Token 已过期: {}", e.getMessage());
                SecurityContextHolder.clearContext();
                if (!isOptionalPath) {
                    request.setAttribute("jwt-exception", e);
                }
            } catch (SignatureException | MalformedJwtException | DecodingException e) {
                log.warn("Token 签名或格式错误: {}", e.getMessage());
                SecurityContextHolder.clearContext();
                if (!isOptionalPath) {
                    request.setAttribute("jwt-exception", e);
                }
            } catch (JwtException | UsernameNotFoundException e) {
                log.warn("Token 验证失败: {}", e.getMessage());
                SecurityContextHolder.clearContext();
                if (!isOptionalPath) {
                    request.setAttribute("jwt-exception", e);
                }
            }

            filterChain.doFilter(request, response);

        } finally {
            // 清理 ThreadLocal，防止内存泄漏
            BaseContext.removeUserInfo();
        }
    }

    /**
     * 从请求中提取 Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtProperties.getUserTokenName());
        if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        // 也支持不带 Bearer 前缀的直接 Token
        return bearerToken;
    }

    /**
     * 构建 UserInfo 用于 ThreadLocal
     */
    private UserInfo buildUserInfo(String userId, Claims claims) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(userId);

        // 从 Claims 中获取其他用户信息
        Object nickname = claims.get("nickname");
        if (nickname != null) {
            userInfo.setNickname(nickname.toString());
        }

        Object avatar = claims.get("avatar");
        if (avatar != null) {
            userInfo.setAvatar(avatar.toString());
        }

        Object userType = claims.get("userType");
        if (userType != null) {
            userInfo.setUserType(Byte.valueOf(userType.toString()));
        }

        return userInfo;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 这些路径不需要 JWT 过滤（由 SecurityConfig 的 permitAll 处理）
        String path = request.getRequestURI();
        return path.startsWith("/api/user/login") ||
               path.startsWith("/api/user/refresh") ||
               path.startsWith("/api/user/create/account") ||
               path.startsWith("/api/user/forget/password") ||
               path.startsWith("/api/banner") ||
               isPublicProductPath(path) ||
               path.startsWith("/api/category") ||
               path.startsWith("/api/notice") ||
               path.startsWith("/api/about") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/swagger-ui.html") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/");
    }

    private boolean isPublicProductPath(String path) {
        return path.startsWith("/api/product")
                && !path.contains("/admin/");
    }
}
