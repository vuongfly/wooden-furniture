package com.woodenfurniture.base;

import com.woodenfurniture.config.excel.SimpleExcelConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Helper class for Excel import operations including natural ID support.
 */
@Slf4j
public class ExcelImportHelper {

    /**
     * Process entities for import, updating existing records if natural IDs match.
     *
     * @param entities     List of entities from the Excel file
     * @param config       Excel configuration defining natural ID fields
     * @param repository   Repository for database operations
     * @param <T>          Entity type
     * @return Map of processed entities (original entity → updated/new entity)
     */
    public static <T extends BaseEntity> Map<T, T> processEntitiesWithNaturalId(
            List<T> entities, 
            SimpleExcelConfig config, 
            JpaRepository<T, ?> repository) {
        
        Map<T, T> processedEntities = new HashMap<>();
        List<SimpleExcelConfig.ColumnMapping> naturalIdFields = getNaturalIdFields(config);
        
        if (naturalIdFields.isEmpty()) {
            // No natural ID fields defined, just use the imported entities as-is
            entities.forEach(entity -> processedEntities.put(entity, entity));
            return processedEntities;
        }
        
        for (T entity : entities) {
            try {
                // Try to find an existing entity by natural ID
                Optional<T> existingEntity = findByNaturalId(entity, naturalIdFields, repository);
                
                if (existingEntity.isPresent()) {
                    // Update the existing entity with new values
                    T updated = updateExistingEntity(existingEntity.get(), entity, config);
                    processedEntities.put(entity, updated);
                } else {
                    // No existing entity found, use the new one
                    processedEntities.put(entity, entity);
                }
            } catch (Exception e) {
                log.error("Error processing entity for natural ID: {}", e.getMessage(), e);
                processedEntities.put(entity, entity); // Fall back to using the original entity
            }
        }
        
        return processedEntities;
    }
    
    /**
     * Get the list of fields that are marked as natural IDs in the configuration.
     *
     * @param config Excel configuration
     * @return List of natural ID field mappings
     */
    private static List<SimpleExcelConfig.ColumnMapping> getNaturalIdFields(SimpleExcelConfig config) {
        List<SimpleExcelConfig.ColumnMapping> naturalIdFields = new ArrayList<>();
        
        if (config.getColumn() != null) {
            for (SimpleExcelConfig.ColumnMapping mapping : config.getColumn()) {
                if (mapping.isNaturalId()) {
                    naturalIdFields.add(mapping);
                }
            }
        }
        
        return naturalIdFields;
    }
    
    /**
     * Find an existing entity by natural ID fields.
     *
     * @param entity           Entity to find
     * @param naturalIdFields  List of natural ID field mappings
     * @param repository       Repository for database operations
     * @param <T>              Entity type
     * @return Optional containing the existing entity if found
     * @throws Exception if an error occurs
     */
    private static <T> Optional<T> findByNaturalId(
            T entity, 
            List<SimpleExcelConfig.ColumnMapping> naturalIdFields, 
            JpaRepository<T, ?> repository) throws Exception {
        
        // Build an example matcher based on natural ID fields
        ExampleMatcher matcher = ExampleMatcher.matching();
        
        // Create a probe instance of the same class
        @SuppressWarnings("unchecked")
        T probe = (T) entity.getClass().getDeclaredConstructor().newInstance();
        
        // Set the natural ID fields on the probe
        for (SimpleExcelConfig.ColumnMapping mapping : naturalIdFields) {
            Field field = entity.getClass().getDeclaredField(mapping.getField());
            field.setAccessible(true);
            Object value = field.get(entity);
            field.set(probe, value);
            
            // Customize the matcher for this field
            matcher = matcher.withMatcher(mapping.getField(), 
                    match -> match.exact());
        }
        
        // Find by example using the probe and matcher
        Example<T> example = Example.of(probe, matcher);
        return repository.findOne(example);
    }
    
    /**
     * Update an existing entity with values from a new entity.
     *
     * @param existingEntity Existing entity from the database
     * @param newEntity      New entity from the Excel file
     * @param config         Excel configuration
     * @param <T>            Entity type
     * @return Updated entity
     * @throws Exception if an error occurs
     */
    private static <T> T updateExistingEntity(T existingEntity, T newEntity, SimpleExcelConfig config) throws Exception {
        // Copy all non-null fields from the new entity to the existing entity
        // except for ID, UUID, created date, and other system fields
        
        for (SimpleExcelConfig.ColumnMapping mapping : config.getColumn()) {
            Field field = newEntity.getClass().getDeclaredField(mapping.getField());
            field.setAccessible(true);
            Object value = field.get(newEntity);
            
            // Only update if the value is not null
            if (value != null) {
                field.set(existingEntity, value);
            }
        }
        
        return existingEntity;
    }
}
