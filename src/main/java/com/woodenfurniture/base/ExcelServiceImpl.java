package com.woodenfurniture.base;

import com.woodenfurniture.config.excel.SimpleExcelConfig;
import com.woodenfurniture.config.excel.SimpleExcelConfigReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

    private final SimpleExcelConfigReader excelConfigReader;

    @Override
    public <T> List<T> importFromExcel(MultipartFile file, SimpleExcelConfig config, Class<T> entityClass) {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Use the first sheet
            return readSheet(sheet, config, entityClass);
        } catch (IOException e) {
            log.error("Error importing Excel file", e);
            throw new RuntimeException("Failed to import Excel file", e);
        }
    }

    @Override
    public <T> Map<Integer, String> importFromExcelWithValidation(
            MultipartFile file,
            SimpleExcelConfig config,
            Class<T> entityClass,
            java.util.function.Function<T, String> validator) {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Use the first sheet
            List<T> entities = readSheet(sheet, config, entityClass);
            Map<Integer, String> validationErrors = new HashMap<>();

            for (int i = 0; i < entities.size(); i++) {
                T entity = entities.get(i);
                String error = validator.apply(entity);
                if (error != null) {
                    validationErrors.put(i + config.getRowIndex() + 1, error);
                }
            }

            return validationErrors;
        } catch (IOException e) {
            log.error("Error importing Excel file with validation", e);
            throw new RuntimeException("Failed to import Excel file", e);
        }
    }

    @Override
    public <T> List<T> importFromExcelWithConfigFile(MultipartFile file, String configPath, Class<T> entityClass) {
        SimpleExcelConfig config = excelConfigReader.readConfig(configPath);
        return importFromExcel(file, config, entityClass);
    }

    @Override
    public <T> Map<Integer, String> importFromExcelWithConfigFileAndValidation(
            MultipartFile file,
            String configPath,
            Class<T> entityClass,
            java.util.function.Function<T, String> validator) {
        SimpleExcelConfig config = excelConfigReader.readConfig(configPath);
        return importFromExcelWithValidation(file, config, entityClass, validator);
    }

    @Override
    public <T> ByteArrayOutputStream exportToExcel(List<T> data, SimpleExcelConfig config) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(config.getName());
            writeHeader(sheet, config);
            writeData(sheet, data, config);
            return writeWorkbook(workbook);
        } catch (IOException e) {
            log.error("Error exporting to Excel", e);
            throw new RuntimeException("Failed to export to Excel", e);
        }
    }

    @Override
    public <T> ByteArrayOutputStream exportToExcelWithResults(
            List<T> data,
            SimpleExcelConfig config,
            Map<T, String> results) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(config.getName());

            // Create header row with an additional "Result" column
            Row headerRow = sheet.createRow(config.getRowIndex());
            int columnIndex = config.getColumnIndex();

            for (SimpleExcelConfig.ColumnMapping mapping : config.getColumn()) {
                Cell cell = headerRow.createCell(columnIndex++);
                cell.setCellValue(mapping.getHeaderExcel());
            }

            // Add the Result column header
            Cell resultHeaderCell = headerRow.createCell(columnIndex);
            resultHeaderCell.setCellValue("Result");

            // Write data with results
            writeDataWithResults(sheet, data, config, results);
            return writeWorkbook(workbook);
        } catch (IOException e) {
            log.error("Error exporting to Excel with results", e);
            throw new RuntimeException("Failed to export to Excel", e);
        }
    }

    @Override
    public <T> ByteArrayOutputStream exportToExcelWithConfigFile(List<T> data, String configPath) {
        SimpleExcelConfig config = excelConfigReader.readConfig(configPath);
        return exportToExcel(data, config);
    }

    @Override
    public <T> ByteArrayOutputStream exportToExcelWithConfigFileAndResults(
            List<T> data,
            String configPath,
            Map<T, String> results) {
        SimpleExcelConfig config = excelConfigReader.readConfig(configPath);
        return exportToExcelWithResults(data, config, results);
    }

    @Override
    public ByteArrayOutputStream generateTemplate(SimpleExcelConfig config) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(config.getName());
            writeHeader(sheet, config);
            return writeWorkbook(workbook);
        } catch (IOException e) {
            log.error("Error generating Excel template", e);
            throw new RuntimeException("Failed to generate Excel template", e);
        }
    }

    @Override
    public ByteArrayOutputStream generateTemplateFromConfigFile(String configPath) {
        SimpleExcelConfig config = excelConfigReader.readConfig(configPath);
        return generateTemplate(config);
    }

    private <T> List<T> readSheet(Sheet sheet, SimpleExcelConfig config, Class<T> entityClass) {
        List<T> entities = new ArrayList<>();

        // Find the header row
        int headerRowIndex = config.getRowIndex();
        Row headerRow = sheet.getRow(headerRowIndex);
        if (headerRow == null) {
            throw new IllegalArgumentException("Header row not found at index " + headerRowIndex);
        }

        // Map header cells to column indices
        Map<String, Integer> headerMap = new HashMap<>();
        for (int i = config.getColumnIndex(); i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                String headerValue = getCellValue(cell).toString();
                headerMap.put(headerValue, i);
            }
        }

        // Read data rows
        for (int i = headerRowIndex + 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            try {
                T entity = entityClass.getDeclaredConstructor().newInstance();

                // Map Excel columns to entity fields
                for (SimpleExcelConfig.ColumnMapping mapping : config.getColumn()) {
                    Integer columnIndex = headerMap.get(mapping.getHeaderExcel());
                    if (columnIndex == null) {
                        log.warn("Column '{}' not found in Excel file", mapping.getHeaderExcel());
                        continue;
                    }

                    Cell cell = row.getCell(columnIndex);
                    if (cell != null) {
                        Object value = getCellValue(cell);
                        setFieldValue(entity, mapping.getField(), value);
                    }
                }

                entities.add(entity);
            } catch (Exception e) {
                log.error("Error processing row {}: {}", i + 1, e.getMessage());
            }
        }

        return entities;
    }

    private void writeHeader(Sheet sheet, SimpleExcelConfig config) {
        Row headerRow = sheet.createRow(config.getRowIndex());

        int columnIndex = config.getColumnIndex();
        for (SimpleExcelConfig.ColumnMapping mapping : config.getColumn()) {
            Cell cell = headerRow.createCell(columnIndex++);
            cell.setCellValue(mapping.getHeaderExcel());
        }
    }

    private <T> void writeData(Sheet sheet, List<T> data, SimpleExcelConfig config) {
        int rowIndex = config.getRowIndex() + 1;

        for (T entity : data) {
            Row row = sheet.createRow(rowIndex++);
            int columnIndex = config.getColumnIndex();

            for (SimpleExcelConfig.ColumnMapping mapping : config.getColumn()) {
                Cell cell = row.createCell(columnIndex++);
                Object value = getFieldValue(entity, mapping.getField());
                setCellValue(cell, value);
            }
        }
    }

    private <T> void writeDataWithResults(Sheet sheet, List<T> data, SimpleExcelConfig config, Map<T, String> results) {
        int rowIndex = config.getRowIndex() + 1;

        for (T entity : data) {
            Row row = sheet.createRow(rowIndex++);
            int columnIndex = config.getColumnIndex();

            // Write entity data
            for (SimpleExcelConfig.ColumnMapping mapping : config.getColumn()) {
                Cell cell = row.createCell(columnIndex++);
                Object value = getFieldValue(entity, mapping.getField());
                setCellValue(cell, value);
            }

            // Write result if available
            Cell resultCell = row.createCell(columnIndex);
            if (results.containsKey(entity)) {
                resultCell.setCellValue(results.get(entity));
            } else {
                resultCell.setCellValue("Success");
            }
        }
    }

    private ByteArrayOutputStream writeWorkbook(Workbook workbook) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return outputStream;
    }

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

    private void setCellValue(Cell cell, Object value) {
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
        } else if (value instanceof java.time.LocalDateTime) {
            cell.setCellValue(((java.time.LocalDateTime) value).toString());
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private Object getFieldValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            log.error("Error getting field value for {}: {}", fieldName, e.getMessage());
            return null;
        }
    }

    private void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            // Convert value to the appropriate type
            if (value != null) {
                if (field.getType() == String.class) {
                    field.set(obj, value.toString());
                } else if (field.getType() == Integer.class || field.getType() == int.class) {
                    field.set(obj, ((Number) value).intValue());
                } else if (field.getType() == Long.class || field.getType() == long.class) {
                    field.set(obj, ((Number) value).longValue());
                } else if (field.getType() == Double.class || field.getType() == double.class) {
                    field.set(obj, ((Number) value).doubleValue());
                } else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                    field.set(obj, Boolean.valueOf(value.toString()));
                } else if (field.getType() == java.time.LocalDateTime.class) {
                    field.set(obj, java.time.LocalDateTime.parse(value.toString()));
                } else {
                    field.set(obj, value);
                }
            }
        } catch (Exception e) {
            log.error("Error setting field value for {}: {}", fieldName, e.getMessage());
        }
    }
}