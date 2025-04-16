package com.woodenfurniture.controller;

import com.woodenfurniture.base.BaseController;
import com.woodenfurniture.dto.response.ApiResponse;
import com.woodenfurniture.dto.response.UserResponse;
import com.woodenfurniture.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

public interface UserController extends BaseController<User, Long> {
    @GetMapping("/myInfo")
    ResponseEntity<ApiResponse<UserResponse>> myInfo();
}
