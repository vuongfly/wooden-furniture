package com.woodenfurniture.controller;

import com.woodenfurniture.base.BaseControllerImpl;
import com.woodenfurniture.entity.User;
import com.woodenfurniture.service.UserService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class UserBaseController extends BaseControllerImpl<User, Long> {
    
    protected final UserService userService;

    public UserBaseController(UserService userService) {
        super(userService, "User");
        this.userService = userService;
    }

} 