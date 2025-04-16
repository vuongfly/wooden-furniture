package com.woodenfurniture.role;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
public class RoleControllerImpl extends RoleBaseController implements RoleController {
    public RoleControllerImpl(RoleService roleService) {
        super(roleService);
    }
} 