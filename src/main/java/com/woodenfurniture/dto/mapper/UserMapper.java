package com.woodenfurniture.dto.mapper;

import com.woodenfurniture.dto.request.UserCreateRequest;
import com.woodenfurniture.dto.request.UserUpdateRequest;
import com.woodenfurniture.dto.response.UserResponse;
import com.woodenfurniture.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserCreateRequest request);
    User toEntity(UserUpdateRequest request);
    User toDto(User entity);
//    @Mapping(target = "password", ignore = true)
    UserResponse toResponse (User entity);
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
