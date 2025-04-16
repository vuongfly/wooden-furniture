package com.woodenfurniture.user;

import com.woodenfurniture.base.BaseController;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class UserBaseController extends BaseController<User, Long, UserRequest, UserResponse> {

    protected final UserService userService;

    public UserBaseController(UserService userService) {
        super(userService, "User");
        this.userService = userService;
    }

} 