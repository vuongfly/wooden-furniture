package com.woodenfurniture.user;

import com.woodenfurniture.base.BaseService;

public interface UserService extends BaseService<User, Long, UserCreateRequest, UserResponse> {
    /**
     * Get the current user's information
     * @return User information
     */
    UserResponse getMyInfo();
}
