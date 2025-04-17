package com.woodenfurniture.role;

import com.woodenfurniture.base.BaseMapper;
import com.woodenfurniture.permission.Permission;
import com.woodenfurniture.permission.PermissionResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingInheritanceStrategy;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_ALL_FROM_CONFIG)
public interface RoleMapper extends BaseMapper<Role, RoleResponse> {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    @Override
//    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "toPermissionResponses")
//    @InheritConfiguration(name = "baseMapper")
    RoleResponse toDto(Role role);

    @Override
    default Role toEntity(Object request) {
        if (!(request instanceof RoleRequest)) {
            throw new IllegalArgumentException("Request must be of type RoleRequest");
        }
        return toRoleEntity((RoleRequest) request);
    }

    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "permissions", source = "permissionNames", qualifiedByName = "toPermissions")
    Role toRoleEntity(RoleRequest request);

    @Override
    default void updateEntityFromDto(Object request, @MappingTarget Role entity) {
        if (!(request instanceof RoleRequest)) {
            throw new IllegalArgumentException("Request must be of type RoleRequest");
        }
        updateRoleEntityFromDto((RoleRequest) request, entity);
    }

    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "permissions", source = "permissionNames", qualifiedByName = "toPermissions")
    void updateRoleEntityFromDto(RoleRequest request, @MappingTarget Role entity);

    @Named("toPermissions")
    default Set<Permission> toPermissions(Set<String> permissionNames) {
        if (permissionNames == null) return null;
        return permissionNames.stream()
                .map(name -> Permission.builder().name(name).build())
                .collect(Collectors.toSet());
    }

    @Named("toPermissionResponses")
    default Set<PermissionResponse> toPermissionResponses(Set<Permission> permissions) {
        if (permissions == null) return null;
        return permissions.stream()
                .map(permission -> PermissionResponse.builder()
                        .name(permission.getName())
                        .description(permission.getDescription())
                        .build())
                .collect(Collectors.toSet());
    }
} 