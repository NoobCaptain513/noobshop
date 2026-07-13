package com.app.noobshop.security.filter;

import com.app.noobshop.common.constant.JwtTokenClaimsConstant;
import com.app.noobshop.common.context.BaseContext;
import com.app.noobshop.common.result.UserInfo;
import com.app.noobshop.common.util.JwtUtils;
import com.app.noobshop.properties.JwtProperties;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * JWT 认证过滤器 - 替代 Shiro 的 JwtFilter
 * 继承 OncePerRequestFilter 确保每个请求只执行一次
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;

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

                // 从 Claims 中获取角色信息（如果有）
                List<GrantedAuthority> authorities = extractAuthorities(claims);

                // 构建 UserDetails
                UserDetails userDetails = new User(userId, "", authorities);

                // 构建认证对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                token,
                                authorities
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
            } catch (JwtException e) {
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
     * 从 Claims 中提取权限信息
     */
    private List<GrantedAuthority> extractAuthorities(Claims claims) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // 尝试从 Claims 获取角色
        Object rolesObj = claims.get("roles");
        if (rolesObj != null) {
            if (rolesObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) rolesObj;
                for (String role : roles) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
            } else if (rolesObj instanceof String) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + rolesObj));
            }
        }

        // 尝试从 Claims 获取权限
        Object permsObj = claims.get("permissions");
        if (permsObj != null) {
            if (permsObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> perms = (List<String>) permsObj;
                for (String perm : perms) {
                    authorities.add(new SimpleGrantedAuthority(perm));
                }
            } else if (permsObj instanceof String) {
                authorities.add(new SimpleGrantedAuthority((String) permsObj));
            }
        }

        return authorities;
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
               path.startsWith("/api/user/change/password") ||
               path.startsWith("/api/banner") ||
               path.startsWith("/api/product") ||
               path.startsWith("/api/category") ||
               path.startsWith("/api/notice") ||
               path.startsWith("/api/upload") ||
               path.startsWith("/api/about") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/swagger-ui.html") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/");
    }
}
