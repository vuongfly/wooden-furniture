package com.woodenfurniture.user;

import com.woodenfurniture.base.BaseResponse;
import com.woodenfurniture.role.RoleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse extends BaseResponse<User> {
    private String username;
    private String email;
    private String name;
    private Set<RoleResponse> roles;
}
