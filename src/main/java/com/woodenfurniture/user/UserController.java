package com.woodenfurniture.user;

import com.woodenfurniture.base.ApiResponse;
import com.woodenfurniture.base.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

public interface UserController extends Controller<User, Long, UserRequest, UserResponse> {

    @GetMapping("/myInfo")
    ResponseEntity<ApiResponse<UserResponse>> getMyInfo();
}
