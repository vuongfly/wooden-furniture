package com.woodenfurniture.user;

import com.woodenfurniture.base.BaseMapper;
import com.woodenfurniture.role.RoleMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper extends BaseMapper<User, UserResponse> {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Override
    UserResponse toDto(User entity);

    default UserResponse toResponse(User entity) {
        return toDto(entity);
    }
}
