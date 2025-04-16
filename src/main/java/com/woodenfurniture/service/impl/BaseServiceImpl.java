package com.woodenfurniture.service.impl;

import com.woodenfurniture.common.BaseEntity;
import com.woodenfurniture.config.excel.SimpleExcelConfig;
import com.woodenfurniture.config.excel.SimpleExcelConfigReader;
import com.woodenfurniture.dto.request.BaseSearchRequest;
import com.woodenfurniture.exception.ResourceNotFoundException;
import com.woodenfurniture.mapper.BaseMapper;
import com.woodenfurniture.repository.BaseRepository;
import com.woodenfurniture.service.BaseService;
import com.woodenfurniture.service.ExcelService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.security.util.FieldUtils.getFieldValue;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseServiceImpl<T extends BaseEntity, ID> implements BaseService<T, ID> {

    protected final BaseRepository<T, ID> repository;
    protected final Class<T> entityClass;
    protected final ExcelService excelService;
    protected final BaseMapper<T, ?> mapper;
    protected final SimpleExcelConfigReader excelConfigReader;

    @Override
    @Transactional
    public Object create(Object request) {
        T entity = mapper.toEntity(request);
        entity = repository.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    @Transactional
    public Object update(ID id, Object request) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(entityClass.getSimpleName() + "id: " + id));
        
        mapper.updateEntityFromDto(request, entity);
        entity = repository.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    public Object getById(ID id) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(entityClass.getSimpleName() + "id: " + id));
        return mapper.toDto(entity);
    }

    @Override
    public Object getByUuid(String uuid) {
        T entity = repository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(entityClass.getSimpleName() + "uuid" + uuid));
        return mapper.toDto(entity);
    }

    @Override
    public List<?> getAll() {
        List<T> entities = repository.findByIsDeletedFalse();
        return mapper.toDtoList(entities);
    }

    @Override
    public Page<?> getAll(Pageable pageable) {
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
    public Page<?> search(BaseSearchRequest searchRequest, Pageable pageable) {
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
            
            // Read the configuration
            SimpleExcelConfig config = excelConfigReader.readConfig(configPath);
            
            // Import data from Excel using the configuration
            List<T> entities = excelService.importFromExcelWithConfigFile(file, configPath, entityClass);
            
            // Validate the imported entities
            Map<T, String> validationResults = validateEntities(entities, config);
            
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
                return criteriaBuilder.like(root.get(property), "%" + typedValue + "%");
            case NOT_LIKE:
                return criteriaBuilder.notLike(root.get(property), "%" + typedValue + "%");
            case IN:
                return root.get(property).in(Arrays.asList(typedValue.toString().split(",")));
            case NOT_IN:
                return criteriaBuilder.not(root.get(property).in(Arrays.asList(typedValue.toString().split(","))));
            case BETWEEN:
                String[] values = typedValue.toString().split(",");
                if (values.length != 2) {
                    return null;
                }
                return criteriaBuilder.between(root.get(property), 
                    (Comparable) convertValue(values[0], type),
                    (Comparable) convertValue(values[1], type));
            default:
                return null;
        }
    }

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
     * Validate entities based on configuration
     * @param entities Entities to validate
     * @param config Excel configuration
     * @return Map of entity to validation error message
     */
    @SneakyThrows
    protected Map<T, String> validateEntities(List<T> entities, SimpleExcelConfig config) {
        Map<T, String> validationErrors = new HashMap<>();
        
        for (T entity : entities) {
            StringBuilder errorMessage = new StringBuilder();
            
            for (SimpleExcelConfig.ColumnMapping column : config.getColumn()) {
                String fieldName = column.getField();
                Object value = getFieldValue(entity, fieldName);
                
                // Required field validation
                if (column.isRequired() && (value == null || 
                    (value instanceof String && ((String) value).trim().isEmpty()))) {
                    errorMessage.append(String.format("%s is required. ", column.getHeaderExcel()));
                    continue;
                }
                
                // Type validation
                if (value != null) {
                    String typeError = validateFieldType(value, column);
                    if (typeError != null) {
                        errorMessage.append(typeError);
                    }
                }
                
                // Unique validation
                if (column.isUnique() && value != null) {
                    String uniqueError = validateUniqueField(entity, fieldName, value);
                    if (uniqueError != null) {
                        errorMessage.append(uniqueError);
                    }
                }
            }
            
            // Add custom validation if needed
            String customValidationError = validateEntity(entity);
            if (customValidationError != null) {
                errorMessage.append(customValidationError);
            }
            
            // If there are validation errors, add them to the map
            if (errorMessage.length() > 0) {
                validationErrors.put(entity, errorMessage.toString().trim());
            }
        }
        
        return validationErrors;
    }
    
    /**
     * Validate field type
     * @param value Field value
     * @param column Column configuration
     * @return Error message if validation fails, null otherwise
     */
    protected String validateFieldType(Object value, SimpleExcelConfig.ColumnMapping column) {
        if (value == null) {
            return null;
        }
        
        try {
            // First check the basic type
            switch (column.getType()) {
                case STRING:
                    if (!(value instanceof String)) {
                        return String.format("%s must be text. ", column.getHeaderExcel());
                    }
                    break;
                case NUMBER:
                    if (!(value instanceof Number)) {
                        return String.format("%s must be a number. ", column.getHeaderExcel());
                    }
                    break;
                case DATE:
                    if (!(value instanceof java.time.LocalDateTime)) {
                        return String.format("%s must be a valid date. ", column.getHeaderExcel());
                    }
                    break;
                case BOOLEAN:
                    if (!(value instanceof Boolean)) {
                        return String.format("%s must be true or false. ", column.getHeaderExcel());
                    }
                    break;
                case EMAIL:
                    if (!(value instanceof String) || !((String) value).matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                        return String.format("%s must be a valid email address. ", column.getHeaderExcel());
                    }
                    break;
            }
        } catch (Exception e) {
            log.warn("Error validating field type: {}", e.getMessage());
            return String.format("Error validating %s: %s", column.getHeaderExcel(), e.getMessage());
        }
        
        return null;
    }
    
    protected String validateUniqueField(Object entity, String fieldName, Object value) {
        try {
            // Convert field name to method name (e.g., "username" -> "existsByUsername")
            String methodName = "existsBy" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            
            // Get the method from repository
            java.lang.reflect.Method method = repository.getClass().getMethod(methodName, value.getClass());
            
            // Invoke the method
            Boolean exists = (Boolean) method.invoke(repository, value);
            
            if (Boolean.TRUE.equals(exists)) {
                return String.format("%s already exists. ", fieldName);
            }
        } catch (Exception e) {
            // If method doesn't exist or can't be invoked, log warning and return null
            log.warn("Could not validate unique field {} with value {}: {}", fieldName, value, e.getMessage());
        }
        return null;
    }
    
    /**
     * Custom entity validation
     * @param entity Entity to validate
     * @return Error message if validation fails, null otherwise
     */
    protected String validateEntity(T entity) {
        // This method should be overridden by subclasses to implement custom validation
        return null;
    }
} 