package com.woodenfurniture.user;

import com.woodenfurniture.base.BaseService;
import com.woodenfurniture.role.RoleResponse;

import java.util.List;
import java.util.Set;

public interface UserService extends BaseService<User, Long, UserRequest, UserResponse> {
    /**
     * Get the current user's information
     *
     * @return User information
     */
    UserResponse getMyInfo();

    /**
     * Check if a user exists by username
     *
     * @param username Username to check
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Get roles for a user by username
     * 
     * @param username Username to get roles for
     * @return Set of RoleResponse objects
     */
    Set<RoleResponse> getRolesByUsername(String username);
    
    /**
     * Add roles to a user
     * 
     * @param userId User ID
     * @param roleNames List of role names to add
     * @return Updated user response
     */
    UserResponse addRolesToUser(Long userId, List<String> roleNames);
    
    /**
     * Remove roles from a user
     * 
     * @param userId User ID
     * @param roleNames List of role names to remove
     * @return Updated user response
     */
    UserResponse removeRolesFromUser(Long userId, List<String> roleNames);
}