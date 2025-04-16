package com.woodenfurniture.role;

import com.woodenfurniture.base.BaseResponse;
import com.woodenfurniture.permission.PermissionResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse extends BaseResponse<Role> {
    String name;
    String description;
    Set<PermissionResponse> permissions;
} 