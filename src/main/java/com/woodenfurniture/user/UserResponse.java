package com.woodenfurniture.user;

import com.woodenfurniture.base.BaseResponse;
import com.woodenfurniture.role.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserResponse extends BaseResponse<User> {

    private Long id;
    private String uuid;
    private String username;
    private String email;
    private String fullName;
    private boolean isDeleted;
    Set<Role> roles;
}
