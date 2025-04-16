package com.woodenfurniture.permission;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/permissions")
public class PermissionControllerImpl extends PermissionBaseController implements PermissionController {
    public PermissionControllerImpl(PermissionService permissionService) {
        super(permissionService);
    }
} 