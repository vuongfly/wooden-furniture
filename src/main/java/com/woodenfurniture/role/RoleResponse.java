package com.woodenfurniture.role;

import com.woodenfurniture.base.BaseResponse;
import com.woodenfurniture.permission.PermissionResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
public class RoleResponse extends BaseResponse<Role> {
    String name;
    String description;
    Set<PermissionResponse> permissions;
} 