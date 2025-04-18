package com.woodenfurniture.role;

import com.woodenfurniture.base.Controller;
import com.woodenfurniture.permission.PermissionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;

public interface RoleController extends Controller<Role, Long, RoleRequest, RoleResponse> {
    
    /**
     * Get all permissions for a specific role
     * 
     * @param id Role ID
     * @return Set of PermissionResponse objects
     */
    @GetMapping("/{id}/permissions")
    ResponseEntity<Set<PermissionResponse>> getRolePermissions(@PathVariable Long id);
}