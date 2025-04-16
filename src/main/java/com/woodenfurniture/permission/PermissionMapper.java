package com.woodenfurniture.permission;

import com.woodenfurniture.base.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper extends BaseMapper<Permission, PermissionResponse> {
    @Override
    PermissionResponse toDto(Permission entity);

    @Override
    Permission toEntity(Object request);

    @Override
    void updateEntityFromDto(Object request, @MappingTarget Permission entity);
} 