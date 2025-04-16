package com.woodenfurniture.service;

import com.woodenfurniture.base.BaseService;
import com.woodenfurniture.dto.response.UserResponse;
import com.woodenfurniture.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface UserService extends BaseService<User, Long> {
    
//    @Transactional
//    UserResponse create(UserCreateRequest request);

//    UserResponse getById(String userId);

    UserResponse getMyInfo();

//    @Transactional
//    UserResponse update(String userId, UserUpdateRequest request);

    @PreAuthorize("hasAuthority('READ_USER')")
    List<UserResponse> getUsers();
}
