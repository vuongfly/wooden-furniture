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
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController extends BaseController<User, Long, UserResponse> {
    UserService userService;
    
    public UserController(UserService userService) {
        super(userService, "User");
        this.userService = userService;
    }

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreateRequest request){
        log.info("Controller: create User");
        return ApiResponse.<UserResponse>builder()
                .result(userService.create(request))
                .build();
    }

    @GetMapping("/{userId}")
    @PostAuthorize("returnObject.result.username == authentication.name")
    ApiResponse<UserResponse> getById(@PathVariable String userId){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getById(userId))
                .build();
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> myInfo(){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @GetMapping
    ApiResponse<List<UserResponse>> getUsers(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Authorities of user {}: {}", authentication.getName(), authentication.getAuthorities());
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @PutMapping("/{userId}")
    @PostAuthorize("returnObject.result.username == authentication.name")
    ApiResponse<UserResponse> update(@RequestBody @Valid UserUpdateRequest request, @PathVariable String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.update(userId, request))
                .build();
    }
}
