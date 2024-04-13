package com.datamall.apicenter.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelToJsonConverter {

    public static List<String> convertExcelToJson(MultipartFile excelFile) {
        try (InputStream inputStream = excelFile.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);

            // 获取第一个工作表
            Sheet sheet = workbook.getSheetAt(0);

            // 获取表头
            Row headerRow = sheet.getRow(0);
            List<String> header = new ArrayList<>();
            for (Cell cell : headerRow) {
                header.add(cell.getStringCellValue());
            }
            // 获取公式计算器
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            // 获取数据
            List<List<Object>> dataList = new ArrayList<>();
            Iterator<Row> rowIterator = sheet.iterator();
            // 跳过表头行
            rowIterator.next();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                List<Object> rowData = new ArrayList<>();
                for (Cell cell : row) {
                    Object cellValue = getCellValue(cell, formulaEvaluator);
                    rowData.add(cellValue);
                }
                dataList.add(rowData);
            }
            //返回每一项的json的列表
            List<String> jsonList = new ArrayList<>();
            for (List<Object> row : dataList) {
                JSONObject jsonObject = new JSONObject(true); // 使用 TreeMap 保持顺序
                for (int i = 0; i < header.size(); i++) {
                    Object value = "";
                    try {
                        value = row.get(i);
                        if (value instanceof Double && ((Double) value % 1 == 0)) { // 检查是否为整数
                            value = ((Double) value).intValue(); // 将浮点数转换为整数
                        }
                    } catch (Exception e) {
                        value = "";
                    }
                    jsonObject.put(header.get(i), value);
                }
                jsonList.add(JSON.toJSONString(jsonObject, SerializerFeature.PrettyFormat));
            }
            return jsonList;
        } catch (IOException e) {
            throw new RuntimeException("Error processing Excel file: " + e.getMessage(), e);
        }
    }

    private static Object getCellValue(Cell cell, FormulaEvaluator formulaEvaluator) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // 日期格式
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                return cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                CellValue cellValue = formulaEvaluator.evaluate(cell);
                switch (cellValue.getCellType()) {
                    case NUMERIC:
                        return cellValue.getNumberValue();
                    case STRING:
                        return cellValue.getStringValue();
                    case BOOLEAN:
                        return cellValue.getBooleanValue();
                }
            case BLANK:
                return null;
            default:
                return null;
        }
    }

    public static List<String> getHeaderList(MultipartFile excelFile) {
        try (InputStream inputStream = excelFile.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            List<String> header = new ArrayList<>();
            for (Cell cell : headerRow) {
                header.add(cell.getStringCellValue());
            }
            return header;
        } catch (IOException e) {
            throw new RuntimeException("Error processing Excel file: " + e.getMessage(), e);
        }
    }
}
