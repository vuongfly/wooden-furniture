package com.woodenfurniture.service;

import com.woodenfurniture.dto.mapper.UserMapper;
import com.woodenfurniture.dto.request.UserCreateRequest;
import com.woodenfurniture.dto.request.UserUpdateRequest;
import com.woodenfurniture.entity.User;
import com.woodenfurniture.exception.ErrorCode;
import com.woodenfurniture.exception.UserNotFoundException;
import com.woodenfurniture.repository.UserResponsitory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private UserResponsitory repo;
    private UserMapper mapper;
    public User create(UserCreateRequest request) {
        return repo.save(mapper.toEntity(request));
    }

    public User getById(String userId) {
        return repo.findById(userId).orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    public User update(String userId, UserUpdateRequest request) {
        User user = getById(userId);
        user = mapper.toEntity(request);
        return repo.save(user);
    }
}
