package com.woodenfurniture.service;

import com.woodenfurniture.dto.mapper.UserMapper;
import com.woodenfurniture.dto.request.UserCreateRequest;
import com.woodenfurniture.dto.request.UserUpdateRequest;
import com.woodenfurniture.dto.response.UserResponse;
import com.woodenfurniture.entity.User;
import com.woodenfurniture.enums.Role;
import com.woodenfurniture.exception.AppException;
import com.woodenfurniture.exception.ErrorCode;
import com.woodenfurniture.exception.UserNotFoundException;
import com.woodenfurniture.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository repo;
    UserMapper mapper;
    PasswordEncoder passwordEncoder;

    public UserResponse create(UserCreateRequest request) {
        if (repo.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = mapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
//        user.setRoles(roles);

        return mapper.toResponse(repo.save(user));
    }

    public UserResponse getById(String userId) {
        return mapper.toResponse(repo.findById(userId).orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_EXISTED)));
    }

    public UserResponse getMyInfo() {
        SecurityContext context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        log.info("Get info of username: {}", name);

        User user = repo.findByUsername(name)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_EXISTED));

        return mapper.toResponse(user);
    }

    public UserResponse update(String userId, UserUpdateRequest request) {
        User user = repo.findById(userId).orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_EXISTED));
        mapper.updateUser(user, request);
        repo.save(user);
        return mapper.toResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        log.info("In method get Users");
        return repo.findAll().stream()
                .map(mapper::toResponse).toList();
    }
}
