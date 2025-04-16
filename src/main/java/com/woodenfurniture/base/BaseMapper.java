package com.woodenfurniture.base;

import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Base mapper interface for mapping between entities and DTOs
 * @param <E> Entity type
 */
public interface BaseMapper<E extends BaseEntity, D> {
    
    /**
     * Map from entity to DTO
     * @param entity Entity to map
     * @return Mapped DTO
     */
    D toDto(E entity);
    
    /**
     * Map from request object to entity
     * @param request Request object to map
     * @return Mapped entity
     */
    E toEntity(Object request);
    
    /**
     * Map from entity list to DTO list
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
     * @param request Request object with new values
     * @param entity Entity to update
     */
    void updateEntityFromDto(Object request, @MappingTarget E entity);
} 