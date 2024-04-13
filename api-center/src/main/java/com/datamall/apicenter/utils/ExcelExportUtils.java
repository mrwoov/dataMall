package com.datamall.apicenter.utils;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExportUtils {
    // 导出文件到本地
    public static String exportData(List<List<Object>> dataList, List<String> header, String fileName) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < header.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(header.get(i));
            }
            // Create data rows
            int rowNum = 1;
            for (List<Object> rowData : dataList) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < rowData.size(); i++) {
                    Cell cell = row.createCell(i);
                    if (rowData.get(i) instanceof String) {
                        cell.setCellValue((String) rowData.get(i));
                    } else if (rowData.get(i) instanceof Integer) {
                        cell.setCellValue((Integer) rowData.get(i));
                    } else if (rowData.get(i) instanceof Double) {
                        cell.setCellValue((Double) rowData.get(i));
                    }
                    // Add more conditions as needed for other data types
                }
            }
            // Write workbook to file
            try (FileOutputStream fileOut = new FileOutputStream("tmp/"+fileName + ".xlsx")) {
                workbook.write(fileOut);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName + ".xlsx";
    }


    public static void exportData(HttpServletResponse response, List<List<Object>> dataList, List<String> header, String fileName) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < header.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(header.get(i));
            }
            // Create data rows
            int rowNum = 1;
            for (List<Object> rowData : dataList) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < rowData.size(); i++) {
                    Cell cell = row.createCell(i);
                    if (rowData.get(i) instanceof String) {
                        cell.setCellValue((String) rowData.get(i));
                    } else if (rowData.get(i) instanceof Integer) {
                        cell.setCellValue((Integer) rowData.get(i));
                    } else if (rowData.get(i) instanceof Double) {
                        cell.setCellValue((Double) rowData.get(i));
                    }
                    // Add more conditions as needed for other data types
                }
            }
            // Set content type and header for response
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            String name = java.net.URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + name + ".xlsx");
            // Write workbook to response output stream
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void download(HttpServletResponse response, String fileName) {
        try {
            // Set content type and header for response
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            // Write workbook to response output stream
            try (FileOutputStream fileOut = new FileOutputStream("tmp/"+fileName)) {
                response.getOutputStream().write(fileOut.toString().getBytes());
            }
            System.out.println("Downloaded");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
