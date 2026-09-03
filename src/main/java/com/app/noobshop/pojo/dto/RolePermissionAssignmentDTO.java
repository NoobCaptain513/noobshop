package com.app.noobshop.pojo.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record RolePermissionAssignmentDTO(
        @NotEmpty Set<@NotNull Long> permissionIds
) {
}
