package com.woodenfurniture.role;

import com.woodenfurniture.permission.PermissionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/roles")
public class RoleControllerImpl extends RoleBaseController implements RoleController {
    public RoleControllerImpl(RoleService roleService) {
        super(roleService);
    }
    
    @Override
    @GetMapping("/{id}/permissions")
    public ResponseEntity<Set<PermissionResponse>> getRolePermissions(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getPermissionsByRoleId(id));
    }
}