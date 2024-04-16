package com.woodenfurniture.service;

import com.woodenfurniture.dto.mapper.UserMapper;
import com.woodenfurniture.dto.request.UserCreateRequest;
import com.woodenfurniture.dto.request.UserUpdateRequest;
import com.woodenfurniture.dto.response.UserResponse;
import com.woodenfurniture.entity.User;
import com.woodenfurniture.exception.AppException;
import com.woodenfurniture.exception.ErrorCode;
import com.woodenfurniture.exception.UserNotFoundException;
import com.woodenfurniture.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository repo;
    UserMapper mapper;
    public UserResponse create(UserCreateRequest request) {
        if (repo.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = mapper.toEntity(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return mapper.toResponse(repo.save(user));
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
