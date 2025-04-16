package com.woodenfurniture.role;

import com.woodenfurniture.base.BaseService;

public interface RoleService extends BaseService<Role, Long, RoleRequest, RoleResponse> {
    /**
     * Check if a role exists by name
     *
     * @param name Role name
     * @return true if role exists, false otherwise
     */
    boolean existsByName(String name);

    RoleResponse getByName(String name);
}