package com.app.noobshop.security;

import com.app.noobshop.common.context.BaseContext;
import com.app.noobshop.common.result.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试用的安全控制器
 * 用于验证 Spring Security 功能
 */
@RestController
@RequestMapping("/api/test/security")
public class SecurityTestController {

    /**
     * 公开接口测试
     */
    @GetMapping("/public")
    public Result<String> publicEndpoint() {
        return Result.success("公开接口访问成功");
    }

    /**
     * 受保护接口测试
     */
    @GetMapping("/protected")
    public Result<String> protectedEndpoint() {
        String userId = BaseContext.getUserId();
        return Result.success("受保护接口访问成功，用户ID: " + userId);
    }

    /**
     * 可选认证接口测试
     */
    @GetMapping("/optional")
    public Result<String> optionalAuthEndpoint() {
        try {
            String userId = BaseContext.getUserId();
            return Result.success("已认证用户访问，用户ID: " + userId);
        } catch (Exception e) {
            return Result.success("匿名用户访问成功");
        }
    }

    /**
     * 管理员角色接口测试
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public Result<String> adminEndpoint() {
        return Result.success("管理员接口访问成功");
    }

    /**
     * 用户角色接口测试
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public Result<String> userEndpoint() {
        return Result.success("用户接口访问成功");
    }

    /**
     * 获取当前用户ID
     */
    @GetMapping("/current-user")
    public Result<String> getCurrentUser() {
        try {
            String userId = BaseContext.getUserId();
            return Result.success(userId);
        } catch (Exception e) {
            return Result.error("未登录");
        }
    }
}
