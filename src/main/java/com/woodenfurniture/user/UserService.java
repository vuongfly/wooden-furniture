package com.woodenfurniture.user;

import com.woodenfurniture.base.BaseService;

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
}
