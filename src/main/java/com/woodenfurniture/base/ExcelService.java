package com.woodenfurniture.base;

import com.woodenfurniture.config.excel.SimpleExcelConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Service for Excel import/export operations
 */
public interface ExcelService {

    /**
     * Import data from Excel file using configuration
     *
     * @param file        Excel file
     * @param config      Excel mapping configuration
     * @param entityClass Class of the entity to create
     * @param <T>         Entity type
     * @return List of imported entities
     */
    <T> List<T> importFromExcel(MultipartFile file, SimpleExcelConfig config, Class<T> entityClass);

    /**
     * Import data from Excel file with validation
     *
     * @param file        Excel file
     * @param config      Excel mapping configuration
     * @param entityClass Class of the entity to create
     * @param validator   Function to validate the entity
     * @param <T>         Entity type
     * @return Map of row index to validation error message
     */
    <T> Map<Integer, String> importFromExcelWithValidation(
            MultipartFile file,
            SimpleExcelConfig config,
            Class<T> entityClass,
            Function<T, String> validator);

    /**
     * Import data from Excel file using configuration file
     *
     * @param file        Excel file
     * @param configPath  Path to the configuration file
     * @param entityClass Class of the entity to create
     * @param <T>         Entity type
     * @return List of imported entities
     */
    <T> List<T> importFromExcelWithConfigFile(MultipartFile file, String configPath, Class<T> entityClass);

    /**
     * Import data from Excel file with validation using configuration file
     *
     * @param file        Excel file
     * @param configPath  Path to the configuration file
     * @param entityClass Class of the entity to create
     * @param validator   Function to validate the entity
     * @param <T>         Entity type
     * @return Map of row index to validation error message
     */
    <T> Map<Integer, String> importFromExcelWithConfigFileAndValidation(
            MultipartFile file,
            String configPath,
            Class<T> entityClass,
            Function<T, String> validator);

    /**
     * Export data to Excel file
     *
     * @param data   Data to export
     * @param config Excel mapping configuration
     * @param <T>    Entity type
     * @return Excel file as ByteArrayOutputStream
     */
    <T> ByteArrayOutputStream exportToExcel(List<T> data, SimpleExcelConfig config);

    /**
     * Export data to Excel file with results
     *
     * @param data    Data to export
     * @param config  Excel mapping configuration
     * @param results Map of entity to result message
     * @param <T>     Entity type
     * @return Excel file as ByteArrayOutputStream
     */
    <T> ByteArrayOutputStream exportToExcelWithResults(
            List<T> data,
            SimpleExcelConfig config,
            Map<T, String> results);

    /**
     * Export data to Excel file using configuration file
     *
     * @param data       Data to export
     * @param configPath Path to the configuration file
     * @param <T>        Entity type
     * @return Excel file as ByteArrayOutputStream
     */
    <T> ByteArrayOutputStream exportToExcelWithConfigFile(List<T> data, String configPath);

    /**
     * Export data to Excel file with results using configuration file
     *
     * @param data       Data to export
     * @param configPath Path to the configuration file
     * @param results    Map of entity to result message
     * @param <T>        Entity type
     * @return Excel file as ByteArrayOutputStream
     */
    <T> ByteArrayOutputStream exportToExcelWithConfigFileAndResults(
            List<T> data,
            String configPath,
            Map<T, String> results);

    /**
     * Generate Excel template
     *
     * @param config Excel mapping configuration
     * @return Excel template as ByteArrayOutputStream
     */
    ByteArrayOutputStream generateTemplate(SimpleExcelConfig config);

    /**
     * Generate Excel template using configuration file
     *
     * @param configPath Path to the configuration file
     * @return Excel template as ByteArrayOutputStream
     */
    ByteArrayOutputStream generateTemplateFromConfigFile(String configPath);
} 