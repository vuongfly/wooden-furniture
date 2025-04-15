package com.woodenfurniture.controller;

import com.woodenfurniture.dto.request.UserCreateRequest;
import com.woodenfurniture.dto.request.UserUpdateRequest;
import com.woodenfurniture.dto.response.ApiResponse;
import com.woodenfurniture.dto.response.UserResponse;
import com.woodenfurniture.entity.User;
import com.woodenfurniture.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController extends BaseController<User, Long, UserResponse> {
    
    private final UserService userService;

    public UserController(UserService userService) {
        super(userService, "User");
        this.userService = userService;
    }

    @GetMapping("/myInfo")
    public ResponseEntity<ApiResponse<UserResponse>> myInfo() {
        UserResponse response = userService.getMyInfo();
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .result(response)
                .build());
    }

}
