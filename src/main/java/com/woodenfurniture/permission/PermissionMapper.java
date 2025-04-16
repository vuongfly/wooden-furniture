package com.woodenfurniture.permission;

import com.woodenfurniture.base.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PermissionMapper extends BaseMapper<Permission, PermissionResponse> {

    PermissionMapper INSTANCE = Mappers.getMapper(PermissionMapper.class);

    @Override
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    PermissionResponse toDto(Permission entity);

    @Override
    default Permission toEntity(Object request) {
        if (!(request instanceof PermissionRequest)) {
            throw new IllegalArgumentException("Request must be of type PermissionRequest");
        }
        return toPermissionEntity((PermissionRequest) request);
    }

    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    Permission toPermissionEntity(PermissionRequest request);

    @Override
    default void updateEntityFromDto(Object request, @MappingTarget Permission entity) {
        if (!(request instanceof PermissionRequest)) {
            throw new IllegalArgumentException("Request must be of type PermissionRequest");
        }
        updatePermissionEntityFromDto((PermissionRequest) request, entity);
    }

    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    void updatePermissionEntityFromDto(PermissionRequest request, @MappingTarget Permission entity);
} 