package com.woodenfurniture.service.impl;

import com.woodenfurniture.common.BaseEntity;
import com.woodenfurniture.config.excel.SimpleExcelConfigReader;
import com.woodenfurniture.dto.request.BaseSearchRequest;
import com.woodenfurniture.exception.ResourceNotFoundException;
import com.woodenfurniture.mapper.BaseMapper;
import com.woodenfurniture.repository.BaseRepository;
import com.woodenfurniture.service.BaseService;
import com.woodenfurniture.service.ExcelService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseServiceImpl<T extends BaseEntity, ID, DTO> implements BaseService<T, ID, DTO> {

    protected final BaseRepository<T, ID> repository;
    protected final Class<T> entityClass;
    protected final ExcelService excelService;
    protected final BaseMapper<T, DTO> mapper;

    @Override
    @Transactional
    public DTO create(DTO dto) {
        T entity = mapper.toEntity(dto);
        entity = repository.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    @Transactional
    public DTO update(ID id, DTO dto) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(entityClass.getSimpleName() + "id: " + id));
        
        mapper.updateEntityFromDto(dto, entity);
        entity = repository.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    public DTO getById(ID id) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(entityClass.getSimpleName() + "id: " + id));
        return mapper.toDto(entity);
    }

    @Override
    public DTO getByUuid(String uuid) {
        T entity = repository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(entityClass.getSimpleName() + "uuid" + uuid));
        return mapper.toDto(entity);
    }

    @Override
    public List<DTO> getAll() {
        List<T> entities = repository.findByIsDeletedFalse();
        return mapper.toDtoList(entities);
    }

    @Override
    public Page<DTO> getAll(Pageable pageable) {
        Page<T> entities = repository.findByIsDeletedFalse(pageable);
        return entities.map(mapper::toDto);
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(entityClass.getSimpleName() + "id: " + id));
        entity.setIsDeleted(true);
        repository.save(entity);
    }

    @Override
    @Transactional
    public void deleteByUuid(String uuid) {
        T entity = repository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(entityClass.getSimpleName() + "uuid" + uuid));
        entity.setIsDeleted(true);
        repository.save(entity);
    }

    @Override
    public Page<DTO> search(BaseSearchRequest searchRequest, Pageable pageable) {
        Specification<T> spec = createSearchSpecification(searchRequest);
        Page<T> entities = repository.findAll(spec, pageable);
        return entities.map(mapper::toDto);
    }

    @Override
    @Transactional
    public ByteArrayOutputStream importData(MultipartFile file) {
        try {
            // Get the configuration file path for this entity
            String configPath = getImportConfigPath();
            
            // Import data from Excel using the configuration
            List<T> entities = excelService.importFromExcelWithConfigFile(file, configPath, entityClass);
            
            // Validate the imported entities
            Map<T, String> validationResults = validateEntities(entities);
            
            // Save valid entities to the database
            for (T entity : entities) {
                if (!validationResults.containsKey(entity)) {
                    repository.save(entity);
                }
            }
            
            // Export the data with validation results
            return excelService.exportToExcelWithConfigFileAndResults(entities, configPath, validationResults);
        } catch (Exception e) {
            log.error("Error importing data for {}: {}", entityClass.getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("Failed to import data: " + e.getMessage(), e);
        }
    }

    @Override
    public ByteArrayOutputStream exportData(BaseSearchRequest searchRequest, Pageable pageable) {
        try {
            // Get the configuration file path for this entity
            String configPath = getExportConfigPath();
            
            // Get data based on search criteria
            List<T> entities;
            if (searchRequest != null) {
                Specification<T> spec = createSearchSpecification(searchRequest);
                entities = repository.findAll(spec);
            } else {
                entities = repository.findByIsDeletedFalse();
            }
            
            // Export data to Excel
            return excelService.exportToExcelWithConfigFile(entities, configPath);
        } catch (Exception e) {
            log.error("Error exporting data for {}: {}", entityClass.getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("Failed to export data: " + e.getMessage(), e);
        }
    }

    /**
     * Create search specification based on the search request
     * @param searchRequest Search request
     * @return Specification object
     */
    protected Specification<T> createSearchSpecification(BaseSearchRequest searchRequest) {
        return (root, query, criteriaBuilder) -> {
            if (searchRequest == null) {
                return null;
            }
            
            // Start with a base predicate that excludes deleted records unless includeDeleted is true
            if (searchRequest.getIncludeDeleted() == null || !searchRequest.getIncludeDeleted()) {
                return criteriaBuilder.equal(root.get("isDeleted"), false);
            }
            
            // Build a list of predicates based on the search criteria
            List<Predicate> predicates = new ArrayList<>();
            
            // Add predicates for each search criterion
            for (BaseSearchRequest.SearchCriteria criterion : searchRequest.getCriteria()) {
                if (criterion == null || criterion.getProperty() == null || criterion.getOperator() == null) {
                    continue;
                }
                
                Predicate predicate = createPredicate(root, criteriaBuilder, criterion);
                if (predicate != null) {
                    predicates.add(predicate);
                }
            }
            
            // Combine all predicates with AND
            return predicates.isEmpty() ? null : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * Create a predicate for a single search criterion
     * @param root Root path
     * @param criteriaBuilder Criteria builder
     * @param criterion Search criterion
     * @return Predicate
     */
    protected Predicate createPredicate(jakarta.persistence.criteria.Root<T> root, 
                                       jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
                                       BaseSearchRequest.SearchCriteria criterion) {
        String property = criterion.getProperty();
        BaseSearchRequest.SearchOperator operator = criterion.getOperator();
        String value = criterion.getValue();
        BaseSearchRequest.FieldType type = criterion.getType();
        
        // Handle null checks
        if (operator == BaseSearchRequest.SearchOperator.IS_NULL) {
            return criteriaBuilder.isNull(root.get(property));
        }
        
        if (operator == BaseSearchRequest.SearchOperator.IS_NOT_NULL) {
            return criteriaBuilder.isNotNull(root.get(property));
        }
        
        // Handle value-based operators
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        // Convert value to the appropriate type
        Object typedValue = convertValue(value, type);
        if (typedValue == null) {
            return null;
        }
        
        // Create predicate based on operator
        switch (operator) {
            case EQUALS:
                return criteriaBuilder.equal(root.get(property), typedValue);
            case NOT_EQUALS:
                return criteriaBuilder.notEqual(root.get(property), typedValue);
            case GREATER_THAN:
                return criteriaBuilder.greaterThan(root.get(property), (Comparable) typedValue);
            case LESS_THAN:
                return criteriaBuilder.lessThan(root.get(property), (Comparable) typedValue);
            case GREATER_THAN_OR_EQUALS:
                return criteriaBuilder.greaterThanOrEqualTo(root.get(property), (Comparable) typedValue);
            case LESS_THAN_OR_EQUALS:
                return criteriaBuilder.lessThanOrEqualTo(root.get(property), (Comparable) typedValue);
            case LIKE:
                return criteriaBuilder.like(root.get(property), "%" + value + "%");
            case NOT_LIKE:
                return criteriaBuilder.notLike(root.get(property), "%" + value + "%");
            case IN:
                return root.get(property).in(Arrays.asList(value.split(",")));
            case NOT_IN:
                return criteriaBuilder.not(root.get(property).in(Arrays.asList(value.split(","))));
            case BETWEEN:
                String[] parts = value.split(",");
                if (parts.length != 2) {
                    return null;
                }
                Object startValue = convertValue(parts[0], type);
                Object endValue = convertValue(parts[1], type);
                if (startValue == null || endValue == null) {
                    return null;
                }
                return criteriaBuilder.between(root.get(property), (Comparable) startValue, (Comparable) endValue);
            default:
                return null;
        }
    }
    
    /**
     * Convert string value to the appropriate type
     * @param value String value
     * @param type Field type
     * @return Converted value
     */
    protected Object convertValue(String value, BaseSearchRequest.FieldType type) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        try {
            switch (type) {
                case STRING:
                    return value;
                case NUMBER:
                    return Double.parseDouble(value);
                case BOOLEAN:
                    return Boolean.parseBoolean(value);
                case DATE:
                    return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE);
                case DATETIME:
                    return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
                case ENUM:
                    // For enum types, we need to know the specific enum class
                    // This is a simplified approach
                    return value;
                default:
                    return value;
            }
        } catch (Exception e) {
            log.warn("Error converting value '{}' to type {}: {}", value, type, e.getMessage());
            return null;
        }
    }
    
    /**
     * Get the path to the import configuration file
     * @return Configuration file path
     */
    protected abstract String getImportConfigPath();
    
    /**
     * Get the path to the export configuration file
     * @return Configuration file path
     */
    protected abstract String getExportConfigPath();
    
    /**
     * Validate entities
     * @param entities Entities to validate
     * @return Map of entity to validation error message
     */
    protected abstract Map<T, String> validateEntities(List<T> entities);
} 