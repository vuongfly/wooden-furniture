package com.woodenfurniture.base;

import com.woodenfurniture.config.excel.SimpleExcelConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Helper class for importing Excel files and validating the data
 */
@Slf4j
public class ExcelImportHelper {

    /**
     * Import Excel file, preserve the original data, and add a Result column with validation results
     *
     * @param file             Excel file to import
     * @param config           Excel config
     * @param entities         List of entities parsed from the Excel file
     * @param validationResults Map of entity to validation result
     * @return ByteArrayOutputStream with the Excel file containing validation results
     */
    public static <T> ByteArrayOutputStream importAndValidate(
            MultipartFile file,
            SimpleExcelConfig config,
            List<T> entities,
            Map<T, String> validationResults) {
            
        try (Workbook originalWorkbook = WorkbookFactory.create(file.getInputStream())) {
            // Get the original sheet
            Sheet sheet = originalWorkbook.getSheetAt(0);
            
            // Get header row
            int headerRowIndex = config.getRowIndex();
            Row headerRow = sheet.getRow(headerRowIndex);
            if (headerRow == null) {
                throw new IllegalArgumentException("Header row not found at index " + headerRowIndex);
            }
            
            // Add "Result" header to the last column
            int lastCellNum = headerRow.getLastCellNum();
            Cell resultHeaderCell = headerRow.createCell(lastCellNum);
            resultHeaderCell.setCellValue("Result");
            
            // Add validation results to each data row
            int entitiesIndex = 0;
            for (int i = headerRowIndex + 1; i <= sheet.getLastRowNum() && entitiesIndex < entities.size(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                T entity = entities.get(entitiesIndex++);
                Cell resultCell = row.createCell(lastCellNum);
                
                if (validationResults.containsKey(entity)) {
                    resultCell.setCellValue(validationResults.get(entity));
                } else {
                    resultCell.setCellValue("Success");
                }
            }
            
            // Write the modified workbook to output stream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            originalWorkbook.write(outputStream);
            return outputStream;
            
        } catch (IOException e) {
            log.error("Error importing Excel file with validation", e);
            throw new RuntimeException("Failed to import Excel file", e);
        }
    }
}
