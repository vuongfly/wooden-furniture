package com.woodenfurniture.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for search requests.
 * Provides dynamic search criteria that can be used across different entities.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseSearchRequest {
    
    /**
     * List of search criteria
     */
    private List<SearchCriteria> criteria = new ArrayList<>();
    
    /**
     * Flag to include soft-deleted records in the search results
     */
    private Boolean includeDeleted = false;
    
    /**
     * Search criteria class
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchCriteria {
        /**
         * Field name to search
         */
        private String property;
        
        /**
         * Search operator
         */
        private SearchOperator operator;
        
        /**
         * Search value
         */
        private String value;
        
        /**
         * Field type
         */
        private FieldType type;
    }
    
    /**
     * Search operators
     */
    public enum SearchOperator {
        EQUALS,
        NOT_EQUALS,
        GREATER_THAN,
        LESS_THAN,
        GREATER_THAN_OR_EQUALS,
        LESS_THAN_OR_EQUALS,
        LIKE,
        NOT_LIKE,
        IN,
        NOT_IN,
        IS_NULL,
        IS_NOT_NULL,
        BETWEEN
    }
    
    /**
     * Field types
     */
    public enum FieldType {
        STRING,
        NUMBER,
        BOOLEAN,
        DATE,
        DATETIME,
        ENUM
    }
} 