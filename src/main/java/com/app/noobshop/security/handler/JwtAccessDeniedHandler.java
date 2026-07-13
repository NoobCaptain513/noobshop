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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT 访问拒绝处理器 - 处理权限不足请求
 * 替代 Shiro 的 ShiroExceptionHandler.handleUnauthorizedException()
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.warn("权限不足: {} {} - {}",
            request.getMethod(),
            request.getRequestURI(),
            accessDeniedException.getMessage()
        );

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        Result<Object> result = Result.error(
            HttpStatus.FORBIDDEN.value(),
            ResultCode.PERMISSION_DENIED.getCode(),
            MessageConstant.PERMISSION_DENIED
        );

        objectMapper.writeValue(response.getOutputStream(), result);
    }
}
