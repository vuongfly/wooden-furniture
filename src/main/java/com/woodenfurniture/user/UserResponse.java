package com.woodenfurniture.user;

import com.woodenfurniture.base.BaseResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse extends BaseResponse<User> {
    
    private Long id;
    private String uuid;
    private String username;
    private String email;
    private String fullName;
    private boolean isDeleted;

}
