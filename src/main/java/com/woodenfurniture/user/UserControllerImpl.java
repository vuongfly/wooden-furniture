package com.woodenfurniture.user;

import com.woodenfurniture.base.ApiResponse;
import com.woodenfurniture.base.BaseController;
import com.woodenfurniture.role.RoleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserControllerImpl extends BaseController<User, Long, UserRequest, UserResponse> implements UserController {

    private final UserService userService;

    public UserControllerImpl(UserService service) {
        super(service, "User");
        this.userService = service;
    }

    @Override
    @GetMapping("/myInfo")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Log for debugging
        System.out.println("Authentication: " + authentication);
        System.out.println("Username: " + username);
        System.out.println("Is authenticated: " + authentication.isAuthenticated());
        System.out.println("Authorities: " + authentication.getAuthorities());
        
        UserResponse response = userService.getMyInfo();
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .code(200)
                .message("User info retrieved successfully")
                .result(response)
                .build());
    }
    
    @Override
    @GetMapping("/{username}/roles")
    public ResponseEntity<Set<RoleResponse>> getUserRoles(@PathVariable String username) {
        return ResponseEntity.ok(userService.getRolesByUsername(username));
    }
    
    @Override
    @GetMapping("/myRoles")
    public ResponseEntity<Set<RoleResponse>> getMyRoles() {
        return ResponseEntity.ok(userService.getMyRoles());
    }
    
    @Override
    @PostMapping("/{id}/roles/add")
    public ResponseEntity<ApiResponse<UserResponse>> addRolesToUser(
            @PathVariable Long id, 
            @RequestBody UserRolesRequest request) {
        
        UserResponse response = userService.addRolesToUser(id, request.getRoleNames());
        
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Roles added successfully")
                .result(response)
                .build());
    }
    
    @Override
    @PostMapping("/{id}/roles/remove")
    public ResponseEntity<ApiResponse<UserResponse>> removeRolesFromUser(
            @PathVariable Long id, 
            @RequestBody UserRolesRequest request) {
        
        UserResponse response = userService.removeRolesFromUser(id, request.getRoleNames());
        
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Roles removed successfully")
                .result(response)
                .build());
    }
}