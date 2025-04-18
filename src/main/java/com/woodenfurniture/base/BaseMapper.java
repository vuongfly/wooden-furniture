package com.woodenfurniture.base;

import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Base mapper interface for mapping between entities and DTOs
 *
 * @param <E> Entity type that extends BaseEntity
 * @param <D> DTO type that extends BaseResponse
 */
public interface BaseMapper<E extends BaseEntity, D extends BaseResponse<E>> {

    /**
     * Map from entity to DTO.
     * All implementation methods should include the base field mappings.
     *
     * @param entity Entity to map
     * @return Mapped DTO
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "isDeleted", source = "isDeleted")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "lastModifiedBy", source = "lastModifiedBy")
    @Mapping(target = "version", source = "version")
    D toDto(E entity);

    /**
     * Map from request object to entity
     *
     * @param request Request object to map
     * @return Mapped entity
     */
    E toEntity(Object request);

    /**
     * Map from entity list to DTO list
     *
     * @param entities Entities to map
     * @return Mapped DTOs
     */
    default List<D> toDtoList(List<E> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Map from request list to entity list
     *
     * @param requests Requests to map
     * @return Mapped entities
     */
    default List<E> toEntityList(List<Object> requests) {
        if (requests == null) {
            return null;
        }
        return requests.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Update entity from request object
     *
     * @param request Request object with new values
     * @param entity  Entity to update
     */
    void updateEntityFromDto(Object request, @MappingTarget E entity);
}