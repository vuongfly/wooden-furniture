package com.woodenfurniture.base;

import com.woodenfurniture.config.excel.SimpleExcelConfig;
import com.woodenfurniture.config.excel.SimpleExcelConfigReader;
import com.woodenfurniture.exception.ResourceNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
public abstract class BaseServiceImpl<T extends BaseEntity, ID, Req extends BaseRequest<T>, Res extends BaseResponse<T>>
        implements BaseService<T, ID, Req, Res> {

    protected final BaseRepository<T, ID> repository;
    protected final Class<T> entityClass;
    protected final ExcelService excelService;
    protected final BaseMapper<T, Res> mapper;
    protected final SimpleExcelConfigReader excelConfigReader;

    @Override
    @Transactional
    public Res create(Req request) {
        T entity = mapper.toEntity(request);
        entity = repository.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    @Transactional
    public Res update(ID id, Req request) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(entityClass.getSimpleName() + "id: " + id));

        mapper.updateEntityFromDto(request, entity);
        entity = repository.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    public Res getById(ID id) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(entityClass.getSimpleName() + "id: " + id));
        return mapper.toDto(entity);
    }

    @Override
    public Res getByUuid(String uuid) {
        T entity = repository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(entityClass.getSimpleName() + "uuid" + uuid));
        return mapper.toDto(entity);
    }

    @Override
    public List<Res> getAll() {
        List<T> entities = repository.findByIsDeletedFalse();
        return mapper.toDtoList(entities);
    }

    @Override
    public Page<Res> getAll(Pageable pageable) {
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
    public Page<Res> search(BaseSearchRequest searchRequest, Pageable pageable) {
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
            
            // Open the workbook for direct processing
            try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);
                
                // If there's no data in the sheet, return a template
                if (sheet.getPhysicalNumberOfRows() <= 1) {
                    log.info("No data rows found in the Excel file");
                    ByteArrayOutputStream emptyTemplate = excelService.generateTemplateFromConfigFile(configPath);
                    return emptyTemplate;
                }
                
                // Parse entities from the Excel rows manually to have better control
                List<T> entities = new ArrayList<>();
                Map<T, String> validationResults = new HashMap<>();
                
                // If we have rows, use row 0 as the header and process data rows starting from row 1
                Row headerRow = sheet.getRow(0);
                if (headerRow == null) {
                    throw new IllegalArgumentException("Header row not found");
                }
                
                // Map headers to column indices
                Map<String, Integer> headerToIndex = new HashMap<>();
                for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                    Cell cell = headerRow.getCell(i);
                    if (cell != null && cell.getCellType() == CellType.STRING) {
                        headerToIndex.put(cell.getStringCellValue(), i);
                    }
                }
                
                // Process data rows
                for (int rowIndex = 1; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
                    Row dataRow = sheet.getRow(rowIndex);
                    if (dataRow == null) continue;
                    
                    try {
                        // Create a new entity
                        T entity = entityClass.getDeclaredConstructor().newInstance();
                        entities.add(entity);
                        
                        // Populate entity fields from row data
                        for (SimpleExcelConfig.ColumnMapping mapping : config.getColumn()) {
                            Integer colIndex = headerToIndex.get(mapping.getHeaderExcel());
                            if (colIndex == null) {
                                log.warn("Column '{}' not found in Excel headers", mapping.getHeaderExcel());
                                continue;
                            }
                            
                            Cell cell = dataRow.getCell(colIndex);
                            if (cell != null) {
                                Object rawValue = getCellValue(cell);
                                // first convert according to mapping.type (+ mapping.format for DATE)
                                Object typedValue = convertValueBasedOnMapping(rawValue, mapping, entity);
                                setFieldValue(entity, mapping.getField(), typedValue);
                            }
                        }
                        
                        // Validate the entity
                        StringBuilder errors = new StringBuilder();
                        for (SimpleExcelConfig.ColumnMapping mapping : config.getColumn()) {
                            String fieldName = mapping.getField();
                            Object value = getFieldValue(entity, fieldName);
                            
                            // Required field validation
                            if (mapping.isRequired() && (value == null || 
                                    (value instanceof String && ((String) value).trim().isEmpty()))) {
                                errors.append(String.format("%s is required. ", mapping.getHeaderExcel()));
                                continue;
                            }
                            
                            // Type validation
                            if (value != null) {
                                String typeError = validateFieldType(value, mapping);
                                if (typeError != null) {
                                    errors.append(typeError);
                                }
                            }
                            
                            // Unique validation
                            if (mapping.isUnique() && value != null) {
                                String uniqueError = validateUniqueField(entity, fieldName, value);
                                if (uniqueError != null) {
                                    errors.append(uniqueError);
                                }
                            }
                            
                            // Regex validation
                            if (value != null && value instanceof String && mapping.getRegex() != null) {
                                String stringValue = (String) value;
                                if (!stringValue.matches(mapping.getRegex())) {
                                    if (mapping.getRegexErrorMessage() != null && !mapping.getRegexErrorMessage().isEmpty()) {
                                        errors.append(mapping.getRegexErrorMessage()).append(" ");
                                    } else {
                                        errors.append(String.format("%s has invalid format. ", mapping.getHeaderExcel()));
                                    }
                                }
                            }
                        }
                        
                        // Custom validation
                        String customError = validateEntity(entity);
                        if (customError != null) {
                            errors.append(customError);
                        }
                        
                        // Store validation result
                        if (errors.length() > 0) {
                            validationResults.put(entity, errors.toString().trim());
                        }
                    } catch (Exception e) {
                        log.error("Error processing row {}: {}", rowIndex, e.getMessage(), e);
                    }
                }
                
                // Save valid entities to the database
                for (T entity : entities) {
                    if (!validationResults.containsKey(entity)) {
                        repository.save(entity);
                    }
                }
                
                // Add Result column to the header row
                int resultColIndex = headerRow.getLastCellNum();
                Cell resultHeaderCell = headerRow.createCell(resultColIndex);
                resultHeaderCell.setCellValue("Result");
                
                // Add validation results to data rows
                for (int rowIndex = 1, entityIndex = 0; rowIndex < sheet.getPhysicalNumberOfRows() && entityIndex < entities.size(); rowIndex++) {
                    Row dataRow = sheet.getRow(rowIndex);
                    if (dataRow == null) continue;
                    
                    T entity = entities.get(entityIndex++);
                    Cell resultCell = dataRow.createCell(resultColIndex);
                    
                    if (validationResults.containsKey(entity)) {
                        resultCell.setCellValue(validationResults.get(entity));
                    } else {
                        resultCell.setCellValue("Success");
                    }
                }
                
                // Write the modified workbook to a byte array
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);
                return outputStream;
            }
        } catch (Exception e) {
            log.error("Error importing data for {}: {}", entityClass.getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("Failed to import data: " + e.getMessage(), e);
        }
    }

    /**
     * Helper method to get cell value 
     */
    private Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue();
                }
                return cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return null;
            case ERROR:
                return "#ERROR";
            default:
                return null;
        }
    }
    
    /**
     * Helper method to set field value
     */
    private void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            // Convert value to the appropriate type
            if (value != null) {
                Class<?> fieldType = field.getType();
                String stringValue = value.toString().trim();
                
                if (fieldType == String.class) {
                    field.set(obj, stringValue);
                } else if (fieldType == Integer.class || fieldType == int.class) {
                    field.set(obj, value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(stringValue));
                } else if (fieldType == Long.class || fieldType == long.class) {
                    field.set(obj, value instanceof Number ? ((Number) value).longValue() : Long.parseLong(stringValue));
                } else if (fieldType == Double.class || fieldType == double.class) {
                    field.set(obj, value instanceof Number ? ((Number) value).doubleValue() : Double.parseDouble(stringValue));
                } else if (fieldType == Boolean.class || fieldType == boolean.class) {
                    field.set(obj, value instanceof Boolean ? value : Boolean.valueOf(stringValue));
                } else if (fieldType == java.time.LocalDateTime.class) {
                    if (value instanceof java.time.LocalDateTime) {
                        field.set(obj, value);
                    } else {
                        try {
                            field.set(obj, java.time.LocalDateTime.parse(stringValue));
                        } catch (Exception e) {
                            // Try alternative date formats
                            try {
                                // Try to parse as MM/dd/yyyy
                                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("M/d/yyyy");
                                java.time.LocalDate date = java.time.LocalDate.parse(stringValue, formatter);
                                field.set(obj, date.atStartOfDay());
                            } catch (Exception e2) {
                                log.warn("Could not parse date '{}' with standard formats", stringValue);
                                throw e2;
                            }
                        }
                    }
                } else if (fieldType == java.time.LocalDate.class) {
                    if (value instanceof java.time.LocalDate) {
                        field.set(obj, value);
                    } else if (value instanceof java.time.LocalDateTime) {
                        field.set(obj, ((java.time.LocalDateTime) value).toLocalDate());
                    } else {
                        try {
                            field.set(obj, java.time.LocalDate.parse(stringValue));
                        } catch (Exception e) {
                            // Try alternative date formats
                            try {
                                // Try to parse as MM/dd/yyyy
                                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("M/d/yyyy");
                                java.time.LocalDate date = java.time.LocalDate.parse(stringValue, formatter);
                                field.set(obj, date);
                            } catch (Exception e2) {
                                log.warn("Could not parse date '{}' with standard formats", stringValue);
                                throw e2;
                            }
                        }
                    }
                } else if (fieldType.isEnum()) {
                    // Handle enum types (including Gender)
                    String strValue = stringValue.toUpperCase();
                    try {
                        // Get enum value from string
                        Object enumValue = fieldType.getDeclaredMethod("valueOf", String.class).invoke(null, strValue);
                        field.set(obj, enumValue);
                    } catch (Exception e) {
                        log.warn("Failed to convert '{}' to enum type {}: {}", strValue, fieldType.getName(), e.getMessage());
                        // Try to find a matching enum value (case-insensitive)
                        for (Object enumConstant : fieldType.getEnumConstants()) {
                            if (enumConstant.toString().equalsIgnoreCase(strValue)) {
                                field.set(obj, enumConstant);
                                break;
                            }
                        }
                    }
                } else if (java.util.Set.class.isAssignableFrom(fieldType)) {
                    // Handle Set types, like roles
                    if (fieldName.equals("roles")) {
                        // Special handling for roles
                        java.util.Set<Object> roleSet = new java.util.HashSet<>();
                        
                        // Split by comma if multiple roles are provided
                        String[] roleNames = stringValue.split(",");
                        for (String roleName : roleNames) {
                            // Create a dummy Role object with just the name set
                            // The actual role will be loaded from the database when needed
                            com.woodenfurniture.role.Role role = new com.woodenfurniture.role.Role();
                            role.setName(roleName.trim());
                            roleSet.add(role);
                        }
                        
                        field.set(obj, roleSet);
                    } else {
                        log.warn("Unhandled Set field: {}", fieldName);
                    }
                } else {
                    log.warn("Unhandled field type: {} for field: {}", fieldType.getName(), fieldName);
                    field.set(obj, value);
                }
            }
        } catch (Exception e) {
            log.error("Error setting field value for {}: {}", fieldName, e.getMessage(), e);
        }
    }

    @Override
    public ByteArrayOutputStream exportData(BaseSearchRequest searchRequest, Pageable pageable) {
        try {
            // Get the configuration file path for this entity
            String configPath = getExportConfigPath();
            
            // Get the configuration
            SimpleExcelConfig config = excelConfigReader.readConfig(configPath);
            
            // If SQL file path is defined in the config, use SQL-based export
            if (config.getSqlFilePath() != null && !config.getSqlFilePath().isEmpty()) {
                return excelService.exportToExcelWithConfigFile(null, configPath);
            } else {
                // Get data based on search criteria the traditional way
                List<T> entities;
                if (searchRequest != null) {
                    Specification<T> spec = createSearchSpecification(searchRequest);
                    entities = repository.findAll(spec);
                } else {
                    entities = repository.findByIsDeletedFalse();
                }

                // Export data to Excel
                return excelService.exportToExcelWithConfigFile(entities, configPath);
            }
        } catch (Exception e) {
            log.error("Error exporting data for {}: {}", entityClass.getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("Failed to export data: " + e.getMessage(), e);
        }
    }

    /**
     * Generate an Excel import template based on the JSON config.
     */
    @Override
    @Transactional
    public ByteArrayOutputStream generateTemplate() {
        String configPath = getImportConfigPath();
        return excelService.generateTemplateFromConfigFile(configPath);
    }

    /**
     * Create search specification based on the search request
     *
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
     *
     * @param root            Root path
     * @param criteriaBuilder Criteria builder
     * @param criterion       Search criterion
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
     *
     * @return Configuration file path
     */
    protected abstract String getImportConfigPath();

    /**
     * Get the path to the export configuration file
     *
     * @return Configuration file path
     */
    protected abstract String getExportConfigPath();

    /**
     * Validate entities based on configuration
     *
     * @param entities Entities to validate
     * @param config   Excel configuration
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
                
                // Regex validation for string values
                if (value != null && value instanceof String && column.getRegex() != null && !column.getRegex().isEmpty()) {
                    String stringValue = (String) value;
                    if (!stringValue.matches(column.getRegex())) {
                        if (column.getRegexErrorMessage() != null && !column.getRegexErrorMessage().isEmpty()) {
                            errorMessage.append(column.getRegexErrorMessage()).append(" ");
                        } else {
                            errorMessage.append(String.format("%s has invalid format. ", column.getHeaderExcel()));
                        }
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
     *
     * @param value  Field value
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
                    if (!(value instanceof java.time.LocalDateTime) &&
                        !(value instanceof java.time.LocalDate)) {
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
                case PHONE:
                    if (!(value instanceof String) || !((String) value).matches("^\\+?[0-9\\s\\-().]+$")) {
                        return String.format("%s must be a valid phone number. ", column.getHeaderExcel());
                    }
                    break;
            }
        } catch (Exception e) {
            log.warn("Error validating field type: {}", e.getMessage());
            return String.format("Error validating %s: %s ", column.getHeaderExcel(), e.getMessage());
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
     *
     * @param entity Entity to validate
     * @return Error message if validation fails, null otherwise
     */
    protected String validateEntity(T entity) {
        // This method should be overridden by subclasses to implement custom validation
        return null;
    }

    /**
     * Convert a raw Excel‐cell value to the right Java type
     * based on ColumnMapping.type and, for DATE, its format.
     */
    private Object convertValueBasedOnMapping(Object rawValue,
                                              SimpleExcelConfig.ColumnMapping mapping,
                                              Object entity) {
        if (rawValue == null) {
            return null;
        }
        SimpleExcelConfig.FieldType type = mapping.getType();
        String stringValue = rawValue.toString().trim();
        switch (type) {
            case STRING:
            case EMAIL:
            case PHONE:
                return stringValue;
            case NUMBER:
                try {
                    if (rawValue instanceof Number) {
                        return rawValue;
                    }
                    return Double.parseDouble(stringValue);
                } catch (Exception e) {
                    log.warn("Unable to parse number '{}' for field {}: {}", stringValue, mapping.getField(), e.getMessage());
                    return rawValue;
                }
            case BOOLEAN:
                if (rawValue instanceof Boolean) {
                    return rawValue;
                }
                return Boolean.parseBoolean(stringValue);
            case DATE:
                try {
                    java.lang.reflect.Field field = entity.getClass()
                        .getDeclaredField(mapping.getField());
                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();

                    // if POI already gave us a date object, just use it
                    if (rawValue instanceof java.time.LocalDateTime ||
                        rawValue instanceof java.time.LocalDate) {
                        return rawValue;
                    }

                    // parse string according to your JSON "format"
                    String format = mapping.getFormat();
                    if (format != null && !format.isEmpty()) {
                        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(format);
                        if (fieldType == java.time.LocalDate.class) {
                            return java.time.LocalDate.parse(stringValue, fmt);
                        } else if (fieldType == java.time.LocalDateTime.class) {
                            return java.time.LocalDateTime.parse(stringValue, fmt);
                        }
                    }

                    // fallback to default ISO parsing
                    if (fieldType == java.time.LocalDate.class) {
                        return java.time.LocalDate.parse(stringValue);
                    } else if (fieldType == java.time.LocalDateTime.class) {
                        return java.time.LocalDateTime.parse(stringValue);
                    }
                } catch (Exception e) {
                    log.warn("Could not parse date '{}' for field {}: {}", stringValue, mapping.getField(), e.getMessage());
                }
                return rawValue;
            default:
                return rawValue;
        }
    }
}