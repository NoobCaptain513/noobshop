package com.app.noobshop.security.service;

import com.app.noobshop.infrastructure.redis.connect.RedisConnector;
import com.app.noobshop.infrastructure.redis.generator.RedisKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

/**
 * Invalidates server-side authorities after role or permission assignments change.
 */
@Service
@RequiredArgsConstructor
public class AuthorizationCacheService {

    private final JdbcTemplate jdbcTemplate;

    public void evictUser(Long userId) {
        if (userId == null) {
            return;
        }
        afterCommit(() -> RedisConnector.delete(RedisKeyGenerator.loginUser(userId)));
    }

    public void evictUsersByRole(Long roleId) {
        if (roleId == null) {
            return;
        }
        List<Long> userIds = jdbcTemplate.queryForList(
                "SELECT user_id FROM sys_user_role WHERE role_id = ?", Long.class, roleId);
        afterCommit(() -> userIds.forEach(
                userId -> RedisConnector.delete(RedisKeyGenerator.loginUser(userId))));
    }

    public void evictUsersByPermission(Long permissionId) {
        if (permissionId == null) {
            return;
        }
        List<Long> userIds = jdbcTemplate.queryForList(
                "SELECT DISTINCT ur.user_id "
                        + "FROM sys_user_role ur "
                        + "JOIN sys_role_permission rp ON rp.role_id = ur.role_id "
                        + "WHERE rp.perm_id = ?",
                Long.class,
                permissionId);
        afterCommit(() -> userIds.forEach(
                userId -> RedisConnector.delete(RedisKeyGenerator.loginUser(userId))));
    }

    private void afterCommit(Runnable action) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            action.run();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                action.run();
            }
        });
    }
}
