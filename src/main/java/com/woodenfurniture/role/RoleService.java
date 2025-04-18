package com.woodenfurniture.role;

import com.woodenfurniture.base.BaseService;
import com.woodenfurniture.permission.PermissionResponse;

import java.util.Set;

public interface RoleService extends BaseService<Role, Long, RoleRequest, RoleResponse> {
    /**
     * Check if a role exists by name
     *
     * @param name Role name
     * @return true if role exists, false otherwise
     */
    boolean existsByName(String name);

    RoleResponse getByName(String name);
    
    /**
     * Get permissions for a role by ID
     * 
     * @param id Role ID
     * @return Set of PermissionResponse objects
     */
    Set<PermissionResponse> getPermissionsByRoleId(Long id);
}