package com.woodenfurniture.dto.mapper;

import com.woodenfurniture.dto.request.PermissionRequest;
import com.woodenfurniture.dto.request.UserUpdateRequest;
import com.woodenfurniture.dto.response.PermissionResponse;
import com.woodenfurniture.entity.Permission;
import com.woodenfurniture.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toEntity(PermissionRequest request);
    PermissionResponse toDto(Permission entity);
//    @Mapping(target = "password", ignore = true)
    PermissionResponse toResponse (Permission entity);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
