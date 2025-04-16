package com.woodenfurniture.user;

import com.woodenfurniture.base.ApiResponse;
import com.woodenfurniture.base.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserControllerImpl extends BaseController<User, Long, UserRequest, UserResponse> implements UserController {

    public UserControllerImpl(UserService service) {
        super(service, "User");
    }

    @Override
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserResponse response = ((UserService) service).getMyInfo();
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .code(200)
                .message("User info retrieved successfully")
                .result(response)
                .build());
    }
} 