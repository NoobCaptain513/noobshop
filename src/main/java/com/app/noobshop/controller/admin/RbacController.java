package com.app.noobshop.controller.admin;

import com.app.noobshop.common.result.Result;
import com.app.noobshop.pojo.dto.RolePermissionAssignmentDTO;
import com.app.noobshop.pojo.dto.UserRoleAssignmentDTO;
import com.app.noobshop.security.service.RbacAdministrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.app.noobshop.security.constant.SecurityExpressions.RBAC_MANAGE;

@RestController
@RequestMapping("/api/admin/rbac")
@RequiredArgsConstructor
@PreAuthorize(RBAC_MANAGE)
public class RbacController {

    private final RbacAdministrationService rbacAdministrationService;

    @PutMapping("/users/{userId}/roles")
    public Result<Void> replaceUserRoles(@PathVariable Long userId,
                                         @RequestBody @Valid UserRoleAssignmentDTO request) {
        rbacAdministrationService.replaceUserRoles(userId, request.roleIds());
        return Result.success();
    }

    @PutMapping("/roles/{roleId}/permissions")
    public Result<Void> replaceRolePermissions(
            @PathVariable Long roleId,
            @RequestBody @Valid RolePermissionAssignmentDTO request) {
        rbacAdministrationService.replaceRolePermissions(roleId, request.permissionIds());
        return Result.success();
    }
}
