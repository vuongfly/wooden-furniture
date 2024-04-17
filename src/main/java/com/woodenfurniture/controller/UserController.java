package com.woodenfurniture.controller;

import com.woodenfurniture.dto.request.UserCreateRequest;
import com.woodenfurniture.dto.request.UserUpdateRequest;
import com.woodenfurniture.dto.response.ApiResponse;
import com.woodenfurniture.dto.response.UserResponse;
import com.woodenfurniture.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;
//    @PostMapping
//    ResponseEntity<UserResponse> create(@RequestBody @Valid UserCreateRequest request) {
//        return ResponseEntity.ok(userService.create(request));
//    }

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreateRequest request){
        log.info("Controller: create User");
        return ApiResponse.<UserResponse>builder()
                .result(userService.create(request))
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getById(@PathVariable String userId){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getById(userId))
                .build();
    }

    @GetMapping
    ApiResponse<List<UserResponse>> getUsers(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> update(@RequestBody @Valid UserUpdateRequest request, @PathVariable String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.update(userId, request))
                .build();
    }
}
