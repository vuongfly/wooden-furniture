package com.woodenfurniture.role;

import com.woodenfurniture.base.BaseMapper;
import com.woodenfurniture.config.MapstructConfig;
import com.woodenfurniture.permission.Permission;
import com.woodenfurniture.permission.PermissionMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
    componentModel = "spring",
    config = MapstructConfig.class,
    uses = {PermissionMapper.class}
)
public interface RoleMapper extends BaseMapper<Role, RoleResponse> {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    /**
     * Maps a Role entity to a RoleResponse DTO, ignoring permissions by default
     */
    @Override
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "permissions", ignore = true)
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
}