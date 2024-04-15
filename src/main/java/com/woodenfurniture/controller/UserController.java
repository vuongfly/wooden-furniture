package com.woodenfurniture.controller;

import com.woodenfurniture.dto.request.UserCreateRequest;
import com.woodenfurniture.dto.request.UserUpdateRequest;
import com.woodenfurniture.entity.User;
import com.woodenfurniture.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    @PostMapping
    ResponseEntity<User> create(@RequestBody @Valid UserCreateRequest request) {
        return ResponseEntity.ok(userService.create(request));
    }

    @GetMapping("/{userId}")
    ResponseEntity<User> getById(@PathVariable String userId){
        return ResponseEntity.ok(userService.getById(userId));
    }

    @PutMapping("/{userId}")
    ResponseEntity<User> update(@RequestBody @Valid UserUpdateRequest request, @PathVariable String userId) {
        return ResponseEntity.ok(userService.update(userId, request));
    }
}
