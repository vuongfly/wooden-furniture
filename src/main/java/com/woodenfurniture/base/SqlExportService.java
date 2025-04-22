package com.woodenfurniture.base;

import com.woodenfurniture.config.excel.SimpleExcelConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Service for exporting data from SQL queries to Excel files
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SqlExportService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Export data from a SQL query to Excel based on configuration
     *
     * @param config Excel mapping configuration with SQL query
     * @return Excel file as ByteArrayOutputStream
     */
    public ByteArrayOutputStream exportToExcel(SimpleExcelConfig config) {
        // Load SQL query from file
        String sqlQuery = determineSqlQuery(config);
        
        if (sqlQuery == null || sqlQuery.isEmpty()) {
            throw new IllegalArgumentException("SQL file is empty or could not be read");
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(config.getName());
            
            // Add a title row if config name is available
            int startRow = config.getRowIndex();
            if (config.getName() != null && !config.getName().isEmpty() && startRow > 0) {
                Row titleRow = sheet.createRow(0);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue(config.getName());
                
                // Create title style
                org.apache.poi.ss.usermodel.CellStyle titleStyle = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
                titleFont.setBold(true);
                titleFont.setFontHeightInPoints((short) 14);
                titleStyle.setFont(titleFont);
                titleStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
                titleCell.setCellStyle(titleStyle);
                
                // Get number of columns to merge
                int columnCount = config.getColumn().size();
                if (columnCount > 1) {
                    sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, columnCount - 1));
                }
            }
            
            // Write header row
            writeHeader(sheet, config);
            
            // Execute SQL query and write data
            writeDataFromSql(sheet, config);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream;
        } catch (IOException e) {
            log.error("Error exporting SQL data to Excel", e);
            throw new RuntimeException("Failed to export SQL data to Excel", e);
        }
    }

    /**
     * Write header row to the Excel sheet
     *
     * @param sheet Sheet to write to
     * @param config Excel configuration
     */
    private void writeHeader(Sheet sheet, SimpleExcelConfig config) {
        Row headerRow = sheet.createRow(config.getRowIndex());
        
        // Create cell style for headers
        Workbook workbook = sheet.getWorkbook();
        org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12); // Slightly larger font
        headerStyle.setFont(headerFont);
        
        // Optional: Add background color and border if desired
        headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);
        headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

        int columnIndex = config.getColumnIndex();
        for (SimpleExcelConfig.ColumnMapping mapping : config.getColumn()) {
            Cell cell = headerRow.createCell(columnIndex++);
            cell.setCellValue(mapping.getHeaderExcel());
            cell.setCellStyle(headerStyle);
        }
        
        // We'll auto-size columns after writing the data
    }

    /**
     * Execute SQL query and write results to Excel sheet
     *
     * @param sheet Sheet to write to
     * @param config Excel configuration with SQL query
     */
    private void writeDataFromSql(Sheet sheet, SimpleExcelConfig config) {
        // Get the SQL query from file
        String sqlQuery = determineSqlQuery(config);
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sqlQuery);
        
        int rowIndex = config.getRowIndex() + 1;
        for (Map<String, Object> row : results) {
            Row excelRow = sheet.createRow(rowIndex++);
            int columnIndex = config.getColumnIndex();
            
            // Map columns based on the configuration
            for (SimpleExcelConfig.ColumnMapping mapping : config.getColumn()) {
                Cell cell = excelRow.createCell(columnIndex++);
                Object value = row.get(mapping.getField());
                setCellValue(cell, value, mapping);
            }
        }
        
        // Auto-size columns after all data is written to better fit content
        int columnCount = config.getColumn().size();
        for (int i = config.getColumnIndex(); i < config.getColumnIndex() + columnCount; i++) {
            sheet.autoSizeColumn(i);
            
            // Add a bit of extra width for better readability (10% extra)
            int currentWidth = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, (int)(currentWidth * 1.1));
        }
    }

    /**
     * Set cell value based on the type of data
     *
     * @param cell Cell to set value
     * @param value Value to set
     * @param mapping Column mapping configuration
     */
    /**
     * Loads SQL query from file
     *
     * @param config Excel configuration
     * @return SQL query string
     */
    private String determineSqlQuery(SimpleExcelConfig config) {
        if (!StringUtils.hasText(config.getSqlFilePath())) {
            throw new IllegalArgumentException("SQL file path is required for SQL-based export");
        }
        
        try {
            // Load SQL from file (from classpath resources)
            ClassPathResource resource = new ClassPathResource(config.getSqlFilePath());
            try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                return FileCopyUtils.copyToString(reader);
            }
        } catch (IOException e) {
            log.error("Error loading SQL from file: {}", config.getSqlFilePath(), e);
            throw new RuntimeException("Failed to load SQL from file: " + e.getMessage(), e);
        }
    }
    
    private void setCellValue(Cell cell, Object value, SimpleExcelConfig.ColumnMapping mapping) {
        if (value == null) {
            cell.setBlank();
            return;
        }

        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Date) {
            Date date = (Date) value;
            if (mapping.getFormat() != null) {
                // Use the specified format
                cell.setCellValue(new java.text.SimpleDateFormat(mapping.getFormat()).format(date));
            } else {
                cell.setCellValue(date);
            }
        } else if (value instanceof LocalDateTime) {
            LocalDateTime dateTime = (LocalDateTime) value;
            if (mapping.getFormat() != null) {
                // Use the specified format
                cell.setCellValue(dateTime.format(DateTimeFormatter.ofPattern(mapping.getFormat())));
            } else {
                cell.setCellValue(dateTime.toString());
            }
        } else {
            cell.setCellValue(value.toString());
        }
    }
}
