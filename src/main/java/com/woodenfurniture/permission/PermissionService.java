package com.woodenfurniture.permission;

import com.woodenfurniture.base.BaseService;

public interface PermissionService extends BaseService<Permission, Long, PermissionRequest, PermissionResponse> {
    /**
     * Check if a permission exists by name
     *
     * @param name Permission name
     * @return true if permission exists, false otherwise
     */
    boolean existsByName(String name);
} 