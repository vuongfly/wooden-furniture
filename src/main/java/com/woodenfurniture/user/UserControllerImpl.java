package com.woodenfurniture.user;

import com.woodenfurniture.base.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserControllerImpl extends UserBaseController implements UserController {
    
    public UserControllerImpl(UserService userService) {
        super(userService);
    }
    
    // Override any methods here if you need specific implementation
    // For example:
    // @Override
    // public ResponseEntity<ApiResponse<?>> create(@RequestBody Object request) {
    //     // Custom implementation
    // }


    @GetMapping("/myInfo")
    public ResponseEntity<ApiResponse<UserResponse>> myInfo() {
        UserResponse response = userService.getMyInfo();
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .result(response)
                .build());
    }
} 