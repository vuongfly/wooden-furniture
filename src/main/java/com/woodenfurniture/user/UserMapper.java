package com.woodenfurniture.user;

import com.woodenfurniture.base.BaseMapper;
import com.woodenfurniture.config.MapstructConfig;
import com.woodenfurniture.role.RoleMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(
    componentModel = "spring",
    config = MapstructConfig.class,
    uses = {RoleMapper.class}
)
public interface UserMapper extends BaseMapper<User, UserResponse> {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Override
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "fullName", source = "name")
    @Mapping(target = "roles", ignore = true)
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
    @Mapping(target = "roles", ignore = true) // We'll set roles manually in the service
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
    @Mapping(target = "roles", ignore = true) // We'll set roles manually in the service
    void updateUserEntityFromDto(UserRequest request, @MappingTarget User entity);
}