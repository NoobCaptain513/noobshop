package com.app.noobshop.security;

import com.app.noobshop.common.result.Result;
import com.app.noobshop.common.util.JwtUtils;
import com.app.noobshop.properties.JwtProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Spring Security 集成测试
 * 测试完整的过滤器链和认证流程
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试公开接口 - 登录接口无需认证
     */
    @Test
    void testPublicEndpoint_Login() throws Exception {
        mockMvc.perform(get("/api/user/login"))
                .andExpect(status().isOk());
    }

    /**
     * 测试公开接口 - 商品列表无需认证
     */
    @Test
    void testPublicEndpoint_ProductList() throws Exception {
        mockMvc.perform(get("/api/product/list"))
                .andExpect(status().isOk());
    }

    /**
     * 测试公开接口 - Banner 列表无需认证
     */
    @Test
    void testPublicEndpoint_Banner() throws Exception {
        mockMvc.perform(get("/api/banner/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * 测试公开接口 - 分类无需认证
     */
    @Test
    void testPublicEndpoint_Category() throws Exception {
        mockMvc.perform(get("/api/category/list"))
                .andExpect(status().isOk());
    }

    /**
     * 测试受保护接口 - 无 Token 返回 401
     */
    @Test
    void testProtectedEndpoint_NoToken() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/user/cart"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        Result<? extends Object> response = objectMapper.readValue(responseContent, Result.class);

        assertFalse(response.getSuccess());
        assertEquals(10001, response.getCode()); // NO_TOKEN
    }

    /**
     * 测试受保护接口 - 携带有效 Token
     */
    @Test
    void testProtectedEndpoint_ValidToken() throws Exception {
        String token = generateValidToken("12345");

        mockMvc.perform(get("/api/user/cart")
                        .header(jwtProperties.getUserTokenName(), token))
                .andExpect(status().isOk());
    }

    /**
     * 测试受保护接口 - 过期 Token 返回 401
     */
    @Test
    void testProtectedEndpoint_ExpiredToken() throws Exception {
        String token = generateExpiredToken();

        MvcResult result = mockMvc.perform(get("/api/user/cart")
                        .header(jwtProperties.getUserTokenName(), token))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        Result<? extends Object> response = objectMapper.readValue(responseContent, Result.class);

        assertFalse(response.getSuccess());
        assertEquals(10002, response.getCode()); // ACCESS_TOKEN_EXPIRED
    }

    /**
     * 测试受保护接口 - 无效 Token 返回 401
     */
    @Test
    void testProtectedEndpoint_InvalidToken() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/user/cart")
                        .header(jwtProperties.getUserTokenName(), "invalid.token.here"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        Result<? extends Object> response = objectMapper.readValue(responseContent, Result.class);

        assertFalse(response.getSuccess());
        assertEquals(10004, response.getCode()); // AUTHENTICATION_SIGNATURE_ERROR
    }

    /**
     * 测试可选认证接口 - 无 Token 也能访问
     */
    @Test
    void testOptionalAuth_NoToken() throws Exception {
        mockMvc.perform(get("/api/user/product/comment/123/show"))
                .andExpect(status().isOk());
    }

    /**
     * 测试可选认证接口 - 有 Token 正常访问
     */
    @Test
    void testOptionalAuth_WithToken() throws Exception {
        String token = generateValidToken("12345");

        mockMvc.perform(get("/api/user/product/comment/123/show")
                        .header(jwtProperties.getUserTokenName(), token))
                .andExpect(status().isOk());
    }

    /**
     * 测试 Swagger 文档接口公开
     */
    @Test
    void testSwaggerPublic() throws Exception {
        // /swagger-ui.html 会被重定向到 /swagger-ui/index.html
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    /**
     * 测试静态资源公开
     */
    @Test
    void testStaticResourcesPublic() throws Exception {
        mockMvc.perform(get("/css/style.css"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/js/app.js"))
                .andExpect(status().isOk());
    }

    /**
     * 测试 Bearer Token 格式
     */
    @Test
    void testBearerTokenFormat() throws Exception {
        String token = generateValidToken("12345");

        mockMvc.perform(get("/api/user/cart")
                        .header(jwtProperties.getUserTokenName(), "Bearer " + token))
                .andExpect(status().isOk());
    }

    /**
     * 生成有效 Token
     */
    private String generateValidToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sysUserId", userId);
        claims.put("nickname", "TestUser");
        claims.put("userType", 3);

        return JwtUtils.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims
        );
    }

    /**
     * 生成过期 Token
     */
    private String generateExpiredToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sysUserId", "12345");

        return JwtUtils.createJWT(
                jwtProperties.getUserSecretKey(),
                -1000L, // 已过期
                claims
        );
    }
}
