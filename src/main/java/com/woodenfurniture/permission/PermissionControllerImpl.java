package com.woodenfurniture.permission;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class PermissionControllerImpl extends PermissionBaseController implements PermissionController {
    public PermissionControllerImpl(PermissionService permissionService) {
        super(permissionService);
    }
} 