package com.woodenfurniture.controller;

import com.woodenfurniture.dto.request.UserCreateRequest;
import com.woodenfurniture.dto.request.UserUpdateRequest;
import com.woodenfurniture.dto.response.UserResponse;
import com.woodenfurniture.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users-v2")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserControllerWithResponseEntity {
    UserService userService;

    @PostMapping
    ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserCreateRequest request){
        log.info("Controller: create User");
        return ResponseEntity.ok(userService.create(request));
    }

    @GetMapping("/{userId}")
    ResponseEntity<UserResponse> getById(@PathVariable String userId){
        return ResponseEntity.ok(userService.getById(userId));
    }

    @GetMapping
    ResponseEntity<List<UserResponse>> getUsers(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ResponseEntity.ok(userService.getUsers());
    }

    @PutMapping("/{userId}")
    ResponseEntity<UserResponse> update(@RequestBody @Valid UserUpdateRequest request, @PathVariable String userId) {

        return ResponseEntity.ok(userService.update(userId, request));
    }
}
