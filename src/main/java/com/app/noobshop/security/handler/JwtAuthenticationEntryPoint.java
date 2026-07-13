package com.app.noobshop.security.handler;

import com.app.noobshop.common.constant.MessageConstant;
import com.app.noobshop.common.result.Result;
import com.app.noobshop.common.result.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT 认证入口点 - 处理未认证请求
 * 替代 Shiro 的 ShiroExceptionHandler.handleUnauthenticatedException()
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // 检查是否是 JWT 异常
        Exception jwtException = (Exception) request.getAttribute("jwt-exception");

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Result<Object> result;

        if (jwtException instanceof io.jsonwebtoken.ExpiredJwtException) {
            // Token 过期
            log.warn("Token 已过期: {}", jwtException.getMessage());
            result = Result.error(
                HttpStatus.UNAUTHORIZED.value(),
                ResultCode.ACCESS_TOKEN_EXPIRED.getCode(),
                MessageConstant.TOKEN_EXPIRED
            );
        } else if (jwtException instanceof io.jsonwebtoken.security.SignatureException
                || jwtException instanceof io.jsonwebtoken.MalformedJwtException
                || jwtException instanceof io.jsonwebtoken.io.DecodingException) {
            // Token 签名错误或格式错误
            log.warn("Token 签名或格式错误: {}", jwtException.getMessage());
            result = Result.error(
                HttpStatus.UNAUTHORIZED.value(),
                ResultCode.AUTHENTICATION_SIGNATURE_ERROR.getCode(),
                MessageConstant.TOKEN_INVALID
            );
        } else {
            // 未登录或认证失败
            log.warn("未认证请求: {} {} - {}",
                request.getMethod(),
                request.getRequestURI(),
                authException != null ? authException.getMessage() : "无认证信息"
            );
            result = Result.error(
                HttpStatus.UNAUTHORIZED.value(),
                ResultCode.NO_TOKEN.getCode(),
                MessageConstant.NO_ACCESS_TOKEN
            );
        }

        objectMapper.writeValue(response.getOutputStream(), result);
    }
}
