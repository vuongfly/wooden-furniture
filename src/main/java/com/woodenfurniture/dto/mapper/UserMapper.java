package com.woodenfurniture.dto.mapper;

import com.woodenfurniture.dto.request.UserCreateRequest;
import com.woodenfurniture.dto.request.UserUpdateRequest;
import com.woodenfurniture.dto.response.UserResponse;
import com.woodenfurniture.entity.User;
import com.woodenfurniture.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<User, UserResponse> {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(UserCreateRequest request);
    
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateUser(@MappingTarget User entity, UserUpdateRequest request);
    
    @Override
    UserResponse toDto(User entity);
    
    default UserResponse toResponse(User entity) {
        return toDto(entity);
    }
}
