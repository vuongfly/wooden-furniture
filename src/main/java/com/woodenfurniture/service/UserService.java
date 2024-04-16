package com.woodenfurniture.service;

import com.woodenfurniture.dto.mapper.UserMapper;
import com.woodenfurniture.dto.request.UserCreateRequest;
import com.woodenfurniture.dto.request.UserUpdateRequest;
import com.woodenfurniture.dto.response.UserResponse;
import com.woodenfurniture.entity.User;
import com.woodenfurniture.exception.ErrorCode;
import com.woodenfurniture.exception.UserNotFoundException;
import com.woodenfurniture.repository.UserResponsitory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserResponsitory repo;
    UserMapper mapper;
    public UserResponse create(UserCreateRequest request) {
        return mapper.toResponse(repo.save(mapper.toEntity(request)));
    }

    public UserResponse getById(String userId) {
        return mapper.toResponse(repo.findById(userId).orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_EXISTED)));
    }

    public UserResponse update(String userId, UserUpdateRequest request) {
        User user = repo.findById(userId).orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_EXISTED));
        mapper.updateUser(user, request);
        repo.save(user);
        return mapper.toResponse(user);
    }
}
