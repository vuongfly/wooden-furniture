package com.woodenfurniture.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Base service interface for common CRUD operations
 * @param <T> Entity type
 * @param <ID> ID type
 * @param <R> Request type
 * @param <S> Response type
 */
public interface BaseService<T extends BaseEntity, ID, R extends BaseRequest<T>, S extends BaseResponse<T>> {
    
    /**
     * Create a new entity
     * @param request Request object with data to create the entity
     * @return Created entity response
     */
    S create(R request);
    
    /**
     * Update an existing entity
     * @param id Entity ID
     * @param request Request object with data to update the entity
     * @return Updated entity response
     */
    S update(ID id, R request);
    
    /**
     * Get entity by ID
     * @param id Entity ID
     * @return Entity response
     */
    S getById(ID id);
    
    /**
     * Get entity by UUID
     * @param uuid Entity UUID
     * @return Entity response
     */
    S getByUuid(String uuid);
    
    /**
     * Get all entities
     * @return List of entity responses
     */
    List<S> getAll();
    
    /**
     * Get all entities with pagination
     * @param pageable Pageable object
     * @return Page of entity responses
     */
    Page<S> getAll(Pageable pageable);
    
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
     * @return Page of responses
     */
    Page<S> search(BaseSearchRequest searchRequest, Pageable pageable);
    
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