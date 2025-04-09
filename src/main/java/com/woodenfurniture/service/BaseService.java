package com.woodenfurniture.service;

import com.woodenfurniture.common.BaseEntity;
import com.woodenfurniture.dto.request.BaseSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Base service interface for common CRUD operations
 * @param <T> Entity type
 * @param <ID> ID type
 * @param <DTO> DTO type
 */
public interface BaseService<T extends BaseEntity, ID, DTO> {
    
    /**
     * Create a new entity
     * @param dto DTO with data to create the entity
     * @return Created entity as DTO
     */
    DTO create(DTO dto);
    
    /**
     * Update an existing entity
     * @param id Entity ID
     * @param dto DTO with data to update the entity
     * @return Updated entity as DTO
     */
    DTO update(ID id, DTO dto);
    
    /**
     * Get entity by ID
     * @param id Entity ID
     * @return Entity as DTO
     */
    DTO getById(ID id);
    
    /**
     * Get entity by UUID
     * @param uuid Entity UUID
     * @return Entity as DTO
     */
    DTO getByUuid(String uuid);
    
    /**
     * Get all entities
     * @return List of entities as DTOs
     */
    List<DTO> getAll();
    
    /**
     * Get all entities with pagination
     * @param pageable Pageable object
     * @return Page of entities as DTOs
     */
    Page<DTO> getAll(Pageable pageable);
    
    /**
     * Delete entity by ID (soft delete)
     * @param id Entity ID
     */
    void deleteById(ID id);
    
    /**
     * Delete entity by UUID (soft delete)
     * @param uuid Entity UUID
     */
    void deleteByUuid(String uuid);
    
    /**
     * Search entities based on search criteria
     * @param searchRequest Search request
     * @param pageable Pageable object for pagination and sorting
     * @return Page of DTOs
     */
    Page<DTO> search(BaseSearchRequest searchRequest, Pageable pageable);
    
    /**
     * Import data from Excel file
     * @param file Excel file
     * @return Excel file with validation results
     */
    ByteArrayOutputStream importData(MultipartFile file);
    
    /**
     * Export data to Excel file
     * @param searchRequest Search request
     * @param pageable Pageable object
     * @return Excel file
     */
    ByteArrayOutputStream exportData(BaseSearchRequest searchRequest, Pageable pageable);
} 