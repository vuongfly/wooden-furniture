package com.woodenfurniture.base;

import com.woodenfurniture.config.excel.SimpleExcelConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Helper class for importing Excel files and processing validation results
 */
@Slf4j
public class ExcelImportHelper {

    /**
     * Process Excel file, import data, validate it, and add a Result column
     *
     * @param file             The uploaded Excel file
     * @param workbook         The POI workbook loaded from the file
     * @param config           The Excel configuration
     * @param entities         The list of entities parsed from the Excel
     * @param validationResults The validation results for each entity
     * @return ByteArrayOutputStream with the modified Excel file
     * @throws IOException If there's an error processing the file
     */
    public static <T> ByteArrayOutputStream processImportFile(
            MultipartFile file,
            Workbook workbook,
            SimpleExcelConfig config,
            List<T> entities,
            Map<T, String> validationResults) throws IOException {
        
        Sheet sheet = workbook.getSheetAt(0);
        
        // Debug information
        log.info("Processing Excel file: {}", file.getOriginalFilename());
        log.info("Number of rows in sheet: {}", sheet.getPhysicalNumberOfRows());
        log.info("Number of entities: {}", entities.size());
        
        // Check if there's data in the file
        if (sheet.getPhysicalNumberOfRows() == 0) {
            // Create header row if the sheet is empty
            Row headerRow = sheet.createRow(0);
            
            // Add column headers from config
            int colIndex = 0;
            for (SimpleExcelConfig.ColumnMapping column : config.getColumn()) {
                Cell cell = headerRow.createCell(colIndex++);
                cell.setCellValue(column.getHeaderExcel());
            }
            
            // Add Result column header
            Cell resultCell = headerRow.createCell(colIndex);
            resultCell.setCellValue("Result");
        } else {
            // Get the existing first row (assume it's the header)
            Row headerRow = sheet.getRow(0);
            if (headerRow != null) {
                // Add Result column header
                int lastColIndex = headerRow.getLastCellNum();
                if (lastColIndex == -1) lastColIndex = 0;
                Cell resultCell = headerRow.createCell(lastColIndex);
                resultCell.setCellValue("Result");
                
                // Add validation results to data rows
                int entityIndex = 0;
                for (int i = 1; i < sheet.getPhysicalNumberOfRows() && entityIndex < entities.size(); i++) {
                    Row dataRow = sheet.getRow(i);
                    if (dataRow == null) continue;
                    
                    // Debug logging
                    log.info("Processing row {}", i);
                    for (int j = 0; j < dataRow.getLastCellNum(); j++) {
                        Cell cell = dataRow.getCell(j);
                        if (cell != null) {
                            log.info("  Cell[{}] = {}", j, getCellValueAsString(cell));
                        }
                    }
                    
                    T entity = entities.get(entityIndex++);
                    Cell resultDataCell = dataRow.createCell(lastColIndex);
                    
                    if (validationResults.containsKey(entity)) {
                        resultDataCell.setCellValue(validationResults.get(entity));
                    } else {
                        resultDataCell.setCellValue("Success");
                    }
                }
            }
        }
        
        // Write the modified workbook to a byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return outputStream;
    }
    
    /**
     * Get cell value as string for logging
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "null";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return Double.toString(cell.getNumericCellValue());
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "[BLANK]";
            default:
                return "[" + cell.getCellType() + "]";
        }
    }
}
