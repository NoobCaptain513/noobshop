package com.app.noobshop.security.service;

import com.app.noobshop.common.constant.MessageConstant;
import com.app.noobshop.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RbacAdministrationService {

    private static final String ADMIN_ROLE_CODE = "ROLE_ADMIN";
    private static final String RBAC_MANAGE_PERMISSION = "rbac:manage";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final AuthorizationCacheService authorizationCacheService;

    @Transactional(rollbackFor = Exception.class)
    public void replaceUserRoles(Long userId, Set<Long> roleIds) {
        requireNonEmpty(roleIds);
        if (queryId("SELECT id FROM sys_user WHERE id = :id FOR UPDATE",
                Map.of("id", userId)) == null) {
            throw new BusinessException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        validateAllActive("sys_role", roleIds);
        protectLastAdministrator(userId, roleIds);

        jdbcTemplate.update("DELETE FROM sys_user_role WHERE user_id = :userId",
                Map.of("userId", userId));
        SqlParameterSource[] batch = roleIds.stream()
                .map(roleId -> new MapSqlParameterSource()
                        .addValue("userId", userId)
                        .addValue("roleId", roleId))
                .toArray(SqlParameterSource[]::new);
        jdbcTemplate.batchUpdate(
                "INSERT INTO sys_user_role (user_id, role_id) VALUES (:userId, :roleId)", batch);
        authorizationCacheService.evictUser(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void replaceRolePermissions(Long roleId, Set<Long> permissionIds) {
        requireNonEmpty(permissionIds);
        String roleCode = jdbcTemplate.query(
                "SELECT role_code FROM sys_role WHERE id = :id AND is_enable = 1 FOR UPDATE",
                Map.of("id", roleId),
                resultSet -> resultSet.next() ? resultSet.getString(1) : null);
        if (roleCode == null) {
            throw new BusinessException(MessageConstant.DATA_ERROR);
        }
        validateAllActive("sys_permission", permissionIds);
        protectRbacAdministration(roleCode, permissionIds);

        jdbcTemplate.update("DELETE FROM sys_role_permission WHERE role_id = :roleId",
                Map.of("roleId", roleId));
        SqlParameterSource[] batch = permissionIds.stream()
                .map(permissionId -> new MapSqlParameterSource()
                        .addValue("roleId", roleId)
                        .addValue("permissionId", permissionId))
                .toArray(SqlParameterSource[]::new);
        jdbcTemplate.batchUpdate(
                "INSERT INTO sys_role_permission (role_id, perm_id) "
                        + "VALUES (:roleId, :permissionId)", batch);
        authorizationCacheService.evictUsersByRole(roleId);
    }

    private void validateAllActive(String table, Set<Long> ids) {
        long activeCount = count(
                "SELECT COUNT(*) FROM " + table
                        + " WHERE id IN (:ids) AND is_enable = 1",
                Map.of("ids", ids));
        if (activeCount != ids.size()) {
            throw new BusinessException(MessageConstant.DATA_ERROR);
        }
    }

    private void protectLastAdministrator(Long userId, Set<Long> roleIds) {
        Long adminRoleId = queryId(
                "SELECT id FROM sys_role WHERE role_code = :code AND is_enable = 1 FOR UPDATE",
                Map.of("code", ADMIN_ROLE_CODE));
        if (adminRoleId == null || roleIds.contains(adminRoleId)) {
            return;
        }
        long userIsAdmin = count(
                "SELECT COUNT(*) FROM sys_user_role WHERE user_id = :userId AND role_id = :roleId",
                Map.of("userId", userId, "roleId", adminRoleId));
        if (userIsAdmin == 0) {
            return;
        }
        long otherAdmins = count(
                "SELECT COUNT(DISTINCT ur.user_id) FROM sys_user_role ur "
                        + "JOIN sys_user u ON u.id = ur.user_id AND u.is_enable = 1 "
                        + "WHERE ur.role_id = :roleId AND ur.user_id <> :userId",
                Map.of("roleId", adminRoleId, "userId", userId));
        if (otherAdmins == 0) {
            throw new BusinessException("不能移除系统中最后一个管理员");
        }
    }

    private void protectRbacAdministration(String roleCode, Set<Long> permissionIds) {
        if (!ADMIN_ROLE_CODE.equals(roleCode)) {
            return;
        }
        Long permissionId = queryId(
                "SELECT id FROM sys_permission WHERE perm_code = :code AND is_enable = 1",
                Map.of("code", RBAC_MANAGE_PERMISSION));
        if (permissionId == null || !permissionIds.contains(permissionId)) {
            throw new BusinessException("管理员角色必须保留 RBAC 管理权限");
        }
    }

    private void requireNonEmpty(Set<Long> ids) {
        if (ids == null || ids.isEmpty() || ids.contains(null)) {
            throw new BusinessException(MessageConstant.INPUT_DATA_ERROR);
        }
    }

    private long count(String sql, Map<String, ?> parameters) {
        Long result = jdbcTemplate.queryForObject(sql, parameters, Long.class);
        return result == null ? 0 : result;
    }

    private Long queryId(String sql, Map<String, ?> parameters) {
        List<Long> ids = jdbcTemplate.queryForList(sql, parameters, Long.class);
        return ids.isEmpty() ? null : ids.get(0);
    }
}
