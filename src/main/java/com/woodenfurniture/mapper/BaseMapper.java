package com.woodenfurniture.mapper;

import com.woodenfurniture.common.BaseEntity;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Base mapper interface for mapping between entities and DTOs
 * @param <E> Entity type
 * @param <D> DTO type
 */
public interface BaseMapper<E extends BaseEntity, D> {
    
    /**
     * Map from entity to DTO
     * @param entity Entity to map
     * @return Mapped DTO
     */
    D toDto(E entity);
    
    /**
     * Map from DTO to entity
     * @param dto DTO to map
     * @return Mapped entity
     */
    E toEntity(D dto);
    
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
     * Map from DTO list to entity list
     * @param dtos DTOs to map
     * @return Mapped entities
     */
    default List<E> toEntityList(List<D> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Update entity from DTO
     * @param dto DTO with new values
     * @param entity Entity to update
     */
    void updateEntityFromDto(D dto, @MappingTarget E entity);
} 