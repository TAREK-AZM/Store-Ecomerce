package org.store.api.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.store.api.entity.User;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportService {
    private static final String EXPORT_DIRECTORY = "src/main/resources/exports/";

    @Autowired
    private UserService userService;

    public String exportUsersToExcel() throws Exception {
        // Get users from the existing service
        List<User> users = userService.getAllUsers();

        if (users.isEmpty()) {
            throw new IllegalStateException("No users found to export");
        }

        // Create export directory if it doesn't exist
        Files.createDirectories(Paths.get(EXPORT_DIRECTORY));

        // Generate filename with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = EXPORT_DIRECTORY + "users_" + timestamp + ".xlsx";

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");

            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Username", "Email", "First Name", "Last Name", "Phone Number", "Address"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fill data rows
            int rowNum = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getUsername());
                row.createCell(2).setCellValue(user.getEmail());
                row.createCell(3).setCellValue(user.getFirstName());
                row.createCell(4).setCellValue(user.getLastName());
                row.createCell(5).setCellValue(user.getPhoneNumber());
                row.createCell(6).setCellValue(user.getAddress());
            }

            // Autosize columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Add metadata sheet
            addMetadataSheet(workbook, users.size());

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

    private void addMetadataSheet(Workbook workbook, int recordCount) {
        Sheet metadataSheet = workbook.createSheet("Metadata");

        Row row0 = metadataSheet.createRow(0);
        row0.createCell(0).setCellValue("Export Date:");
        row0.createCell(1).setCellValue(LocalDateTime.now().toString());

        Row row1 = metadataSheet.createRow(1);
        row1.createCell(0).setCellValue("Total Users:");
        row1.createCell(1).setCellValue(recordCount);
    }
}