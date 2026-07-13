package com.app.noobshop.common.handler;


import com.app.noobshop.common.constant.MessageConstant;
import com.app.noobshop.common.exception.BusinessException;
import com.app.noobshop.common.exception.EmptyObjectException;
import com.app.noobshop.common.exception.PayException;
import com.app.noobshop.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Objects;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@Order(3)
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    //TODO 业务异常处理...


    @ExceptionHandler(PayException.class)
    public Result handlePayException() {
        return Result.error(MessageConstant.NETWORK_ERROR);
    }


    @ExceptionHandler(NoResourceFoundException.class)
    public void handleNoResourceFoundException(HttpServletRequest request, NoResourceFoundException e) {
        // 判断是否是 favicon.ico 请求，是则不打印日志
        if ("/favicon.ico".equals(request.getRequestURI())) {
            return;
        }
        // 其他资源找不到的异常，正常打印日志
        log.error("globalExceptionHandler拦截到:{};异常信息:{}", e.getClass(), e.getMessage(), e);
    }

    /**
     * "用户名已存在"
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({DuplicateKeyException.class, SQLIntegrityConstraintViolationException.class})
    public Result DKExceptionHandler(Exception ex) {
        //Duplicate entry 'zhangsan' for key 'employee.idx_username'
        String message = ex.getMessage();
        if (message.contains("Duplicate entry")) {
            return Result.error(MessageConstant.USER_NAME_EXISTS);
        }
        log.error("DK ExceptionHandler拦截到:{};异常信息:{}", ex.getClass(), ex.getMessage());
        return Result.error(MessageConstant.TOM_CAT_ERROR);
    }

    /**
     * BCrypt 用户登录账户校验异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({IllegalArgumentException.class})
    public Result IAEExceptionHandler(Exception ex) {
        String message = ex.getMessage();
        if (message.contains("Invalid salt version")) {
            return Result.error(MessageConstant.LOGIN_ERROR);
        }
        log.error("IAE ExceptionHandler拦截到:{};异常信息:{}", ex.getClass(), ex.getMessage());
        return Result.error(MessageConstant.TOM_CAT_ERROR);
    }

    /**
     * 捕获所有数据库保存数据相关异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({SQLException.class})
    public Result<?> SQLExceptionHandler(Exception ex) {
        log.error(" SQL ExceptionHandler拦截到:{};异常信息:{}", ex.getClass(), ex.getMessage());
        return Result.error(MessageConstant.SQL_MESSAGE_SAVE_ERROR);
    }

    @ExceptionHandler({EmptyObjectException.class})
    public Result<?> EmptyObjectExceptionHandler(Exception ex) {
        log.error("EmptyObject ExceptionHandler拦截到:{};异常信息:{}", ex.getClass(), ex.getMessage());
        return Result.error(MessageConstant.DATA_ERROR);
    }




    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidException(MethodArgumentNotValidException e) {
        // 获取第一个校验失败的提示信息
        String errorMsg = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        return Result.error(errorMsg);
    }

    /**
     * Spring Security 认证异常
     * 令牌过期、无效、未登录
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Object> handleAuthenticationException(AuthenticationException e) {
        log.error("认证失败: {}", e.getMessage());
        return Result.error(MessageConstant.USER_NOT_LOGIN);
    }

    /**
     * Spring Security 权限不足异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Object> handleAccessDeniedException(AccessDeniedException e) {
        log.error("权限不足: {}", e.getMessage());
        return Result.error(MessageConstant.PERMISSION_DENIED);
    }

    /**
     * 凭据无效异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Object> handleBadCredentialsException(BadCredentialsException e) {
        log.error("凭据无效: {}", e.getMessage());
        return Result.error(MessageConstant.LOGIN_ERROR);
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Object> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 全局异常拦截
     * "服务器异常"
     *
     * @param e
     * @return
     */
    @ExceptionHandler
    public Result globalExceptionHandler(Exception e) {
        log.error("完整异常栈:", e);
        log.error("globalExceptionHandler拦截到:{};异常信息:{}", e.getClass(), e.getMessage());
        return Result.error(MessageConstant.TOM_CAT_ERROR+" ,异常信息: "+e.getMessage());
    }
}
