package com.woodenfurniture.dto.mapper;

import com.woodenfurniture.dto.request.RoleRequest;
import com.woodenfurniture.user.UserUpdateRequest;
import com.woodenfurniture.dto.response.RoleResponse;
import com.woodenfurniture.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toEntity(RoleRequest request);
    RoleResponse toDto(Role entity);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
