package com.app.noobshop.security.filter;

import com.app.noobshop.common.constant.JwtTokenClaimsConstant;
import com.app.noobshop.common.context.BaseContext;
import com.app.noobshop.properties.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * JWT 认证过滤器单元测试
 */
class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    // 使用足够长的 UTF-8 字符串作为密钥（HS256 要求至少 256 bit / 32 字节）
    private final String secretKey = "TheSpringSecurityMigrationTestKey1234567890123456";
    private final String tokenName = "Authorization";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(jwtProperties.getUserSecretKey()).thenReturn(secretKey);
        when(jwtProperties.getUserTokenName()).thenReturn(tokenName);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtProperties);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        BaseContext.removeUserInfo();
    }

    /**
     * 测试公开接口 - 无需 Token 也能访问
     */
    @Test
    void testPublicEndpoint_NoToken() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/user/login");
        when(request.getMethod()).thenReturn("POST");
        when(request.getHeader(tokenName)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证过滤器链继续执行
        verify(filterChain).doFilter(request, response);
        // 验证没有设置认证信息
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * 测试受保护接口 - 无 Token 返回 401
     */
    @Test
    void testProtectedEndpoint_NoToken() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/user/cart");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader(tokenName)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证过滤器链继续执行
        verify(filterChain).doFilter(request, response);
    }

    /**
     * 测试有效 Token
     */
    @Test
    void testValidToken() throws ServletException, IOException {
        String userId = "12345";
        String token = generateValidToken(userId);

        when(request.getRequestURI()).thenReturn("/api/user/cart");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader(tokenName)).thenReturn(token);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证认证信息已设置
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userId, SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain).doFilter(request, response);
    }

    /**
     * 测试过期 Token
     */
    @Test
    void testExpiredToken() throws ServletException, IOException {
        String token = generateExpiredToken();

        when(request.getRequestURI()).thenReturn("/api/user/cart");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader(tokenName)).thenReturn(token);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证没有设置认证信息
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        // 验证 request 中设置了异常属性
        verify(request).setAttribute(eq("jwt-exception"), any());
        verify(filterChain).doFilter(request, response);
    }

    /**
     * 测试无效 Token
     */
    @Test
    void testInvalidToken() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/user/cart");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader(tokenName)).thenReturn("invalid.token.here");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证没有设置认证信息
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(request).setAttribute(eq("jwt-exception"), any());
        verify(filterChain).doFilter(request, response);
    }

    /**
     * 测试可选认证路径 - 无 Token 也能访问
     */
    @Test
    void testOptionalAuthPath_NoToken() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/user/product/comment/123/show");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader(tokenName)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * 测试可选认证路径 - 有 Token 正常认证
     */
    @Test
    void testOptionalAuthPath_WithToken() throws ServletException, IOException {
        String userId = "12345";
        String token = generateValidToken(userId);

        when(request.getRequestURI()).thenReturn("/api/user/product/comment/123/show");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader(tokenName)).thenReturn(token);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userId, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    /**
     * 测试 Bearer Token 格式
     */
    @Test
    void testBearerToken() throws ServletException, IOException {
        String userId = "12345";
        String token = generateValidToken(userId);

        when(request.getRequestURI()).thenReturn("/api/user/cart");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader(tokenName)).thenReturn("Bearer " + token);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * 测试 OPTIONS 请求直接放行
     */
    @Test
    void testOptionsRequest() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/user/cart");
        when(request.getMethod()).thenReturn("OPTIONS");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        // 不验证 Token
        verify(request, never()).getHeader(anyString());
    }

    /**
     * 生成有效 Token
     */
    private String generateValidToken(String userId) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtTokenClaimsConstant.SYS_USER_ID, userId);
        claims.put("nickname", "TestUser");
        claims.put("userType", 3);

        return Jwts.builder()
                .claims(claims)
                .signWith(key)
                .expiration(new Date(System.currentTimeMillis() + 3600000)) // 1小时后过期
                .compact();
    }

    /**
     * 生成过期 Token
     */
    private String generateExpiredToken() {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtTokenClaimsConstant.SYS_USER_ID, "12345");

        return Jwts.builder()
                .claims(claims)
                .signWith(key)
                .expiration(new Date(System.currentTimeMillis() - 1000)) // 已过期
                .compact();
    }
}
