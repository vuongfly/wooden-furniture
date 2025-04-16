package com.woodenfurniture.user;

import com.woodenfurniture.base.BaseMapper;
import com.woodenfurniture.role.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<User, UserResponse> {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Override
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "fullName", source = "name")
    UserResponse toDto(User entity);

    @Override
    default User toEntity(Object request) {
        if (!(request instanceof UserRequest)) {
            throw new IllegalArgumentException("Request must be of type UserRequest");
        }
        return toUserEntity((UserRequest) request);
    }

    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "name", source = "fullName")
    @Mapping(target = "age", source = "age")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "dob", source = "dob")
    @Mapping(target = "roles", source = "roles", qualifiedByName = "toRoles")
    User toUserEntity(UserRequest request);

    @Override
    default void updateEntityFromDto(Object request, @MappingTarget User entity) {
        if (!(request instanceof UserRequest)) {
            throw new IllegalArgumentException("Request must be of type UserRequest");
        }
        updateUserEntityFromDto((UserRequest) request, entity);
    }

    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "name", source = "fullName")
    @Mapping(target = "age", source = "age")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "dob", source = "dob")
    @Mapping(target = "roles", source = "roles", qualifiedByName = "toRoles")
    void updateUserEntityFromDto(UserRequest request, @MappingTarget User entity);

    @Named("toRoles")
    default Set<Role> toRoles(List<String> roleNames) {
        if (roleNames == null) return null;
        return roleNames.stream()
                .map(name -> Role.builder().name(name).build())
                .collect(Collectors.toSet());
    }
}
