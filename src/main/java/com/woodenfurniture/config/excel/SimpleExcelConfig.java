package com.woodenfurniture.config.excel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Simple configuration class for Excel import/export mapping using JSON format
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleExcelConfig {

    /**
     * Name of the configuration
     */
    private String name;

    /**
     * Row index where data starts (0-based)
     */
    private int rowIndex;

    /**
     * Column index where data starts (0-based)
     */
    private int columnIndex;
    
    /**
     * Path to SQL file containing query to execute for export
     * If provided, export will be SQL-based rather than entity-based
     */
    private String sqlFilePath;

    /**
     * List of column mappings
     */
    private List<ColumnMapping> column;

    /**
     * Class representing a column mapping
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnMapping {

        /**
         * Header text in the Excel file
         */
        private String headerExcel;

        /**
         * Field name in the entity
         */
        private String field;

        /**
         * Whether the field is required
         */
        private boolean required;

        /**
         * Whether the field must be unique
         */
        private boolean unique;

        /**
         * Whether the field can have multiple values
         */
        private boolean multiple;

        /**
         * Data type of the field
         */
        private FieldType type;

        /**
         * Format for date/time fields
         */
        private String format;

        /**
         * Regular expression for pattern validation
         */
        private String regex;

        /**
         * Custom error message for regex validation failure
         */
        private String regexErrorMessage;
    }

    /**
     * Enum representing field types
     */
    public enum FieldType {
        STRING,
        NUMBER,
        DATE,
        BOOLEAN,
        EMAIL,
        PHONE
    }
}