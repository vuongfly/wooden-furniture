package com.woodenfurniture.user;

import com.woodenfurniture.base.ApiResponse;
import com.woodenfurniture.base.Controller;
import com.woodenfurniture.role.RoleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

public interface UserController extends Controller<User, Long, UserRequest, UserResponse> {

    @GetMapping("/myInfo")
    ResponseEntity<ApiResponse<UserResponse>> getMyInfo();
    
    /**
     * Get roles for a user by username
     * 
     * @param username Username to get roles for
     * @return Set of RoleResponse objects
     */
    @GetMapping("/{username}/roles")
    ResponseEntity<Set<RoleResponse>> getUserRoles(@PathVariable String username);
    
    /**
     * Add roles to a user
     * 
     * @param id User ID
     * @param request Request containing roles to add
     * @return Updated user response
     */
    @PostMapping("/{id}/roles/add")
    ResponseEntity<ApiResponse<UserResponse>> addRolesToUser(
            @PathVariable Long id, 
            @RequestBody UserRolesRequest request);
    
    /**
     * Remove roles from a user
     * 
     * @param id User ID
     * @param request Request containing roles to remove
     * @return Updated user response
     */
    @PostMapping("/{id}/roles/remove")
    ResponseEntity<ApiResponse<UserResponse>> removeRolesFromUser(
            @PathVariable Long id, 
            @RequestBody UserRolesRequest request);
}