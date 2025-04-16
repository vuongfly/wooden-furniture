package com.woodenfurniture.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * Find entity by UUID
     *
     * @param uuid UUID of the entity
     * @return Optional of the entity
     */
    Optional<T> findByUuid(String uuid);

    /**
     * Find entity by code
     *
     * @param code Code of the entity
     * @return Optional of the entity
     */
    Optional<T> findByCode(String code);

    /**
     * Find all entities that are not deleted
     *
     * @return List of entities
     */
    List<T> findByIsDeletedFalse();

    /**
     * Find all entities that are not deleted with pagination
     *
     * @param pageable Pageable object for pagination
     * @return Page of entities
     */
    Page<T> findByIsDeletedFalse(Pageable pageable);
} 