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
    }
} 