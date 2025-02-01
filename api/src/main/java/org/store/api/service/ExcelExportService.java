package org.store.api.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.store.api.entity.User;
import java.lang.reflect.Method;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
@Service
public class ExcelExportService {
    private static final String EXPORT_DIRECTORY = "src/main/resources/exports/";

    public <T> String exportToExcel(List<T> entities, String entityName) throws Exception {
        if (entities == null || entities.isEmpty()) {
            throw new IllegalStateException("No " + entityName + " found to export");
        }

        // Create export directory if it doesn't exist
        Files.createDirectories(Paths.get(EXPORT_DIRECTORY));

        // Generate filename with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = EXPORT_DIRECTORY + entityName.toLowerCase() + "_" + timestamp + ".xlsx";

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(entityName);

            // Get methods and headers using reflection
            List<Method> getterMethods = new ArrayList<>();
            List<String> headers = new ArrayList<>();

            // Get the first entity to analyze its structure
            T sampleEntity = entities.get(0);
            for (Method method : sampleEntity.getClass().getMethods()) {
                String methodName = method.getName();
                if (methodName.startsWith("get") &&
                        !methodName.equals("getClass") &&
                        method.getParameterCount() == 0) {
                    getterMethods.add(method);
                    // Convert getter name to header (e.g., "getId" -> "ID")
                    String header = methodName.substring(3);
                    header = header.substring(0, 1).toUpperCase() +
                            header.substring(1).replaceAll("([A-Z])", " $1").trim();
                    headers.add(header);
                }
            }

            // Create header row with styling
            CellStyle headerStyle = createHeaderStyle(workbook);
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            // Fill data rows
            int rowNum = 1;
            for (T entity : entities) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < getterMethods.size(); i++) {
                    Cell cell = row.createCell(i);
                    Object value = getterMethods.get(i).invoke(entity);
                    if (value != null) {
                        if (value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                }
            }

            // Autosize columns
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Add metadata sheet
            addMetadataSheet(workbook, entities.size(), entityName);

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filename)) {
                workbook.write(fileOut);
            }

            return filename;
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private void addMetadataSheet(Workbook workbook, int recordCount, String entityName) {
        Sheet metadataSheet = workbook.createSheet("Metadata");

        Row row0 = metadataSheet.createRow(0);
        row0.createCell(0).setCellValue("Export Date:");
        row0.createCell(1).setCellValue(LocalDateTime.now().toString());

        Row row1 = metadataSheet.createRow(1);
        row1.createCell(0).setCellValue("Total " + entityName + ":");
        row1.createCell(1).setCellValue(recordCount);
    }
}