package com.woodenfurniture.permission;

import com.woodenfurniture.base.BaseController;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class PermissionBaseController extends BaseController<Permission, Long, PermissionRequest, PermissionResponse> {
    protected final PermissionService permissionService;

    public PermissionBaseController(PermissionService permissionService) {
        super(permissionService, "Permission");
        this.permissionService = permissionService;
    }
} 