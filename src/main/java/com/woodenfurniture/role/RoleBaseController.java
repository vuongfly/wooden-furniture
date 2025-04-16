package com.woodenfurniture.role;

import com.woodenfurniture.base.BaseController;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class RoleBaseController extends BaseController<Role, Long, RoleRequest, RoleResponse> {
    protected final RoleService roleService;

    public RoleBaseController(RoleService roleService) {
        super(roleService, "Role");
        this.roleService = roleService;
    }
} 