package com.woodenfurniture.user;

import com.woodenfurniture.base.BaseController;
import com.woodenfurniture.base.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

public interface UserController extends BaseController<User, Long> {
    @GetMapping("/myInfo")
    ResponseEntity<ApiResponse<UserResponse>> myInfo();
}
