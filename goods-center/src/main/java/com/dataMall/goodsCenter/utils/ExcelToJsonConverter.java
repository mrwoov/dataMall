package com.dataMall.goodsCenter.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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

            // 获取数据
            List<List<Object>> dataList = new ArrayList<>();
            Iterator<Row> rowIterator = sheet.iterator();
            // 跳过表头行
            rowIterator.next();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                List<Object> rowData = new ArrayList<>();
                for (Cell cell : row) {
                    rowData.add(getCellValue(cell));
                }
                dataList.add(rowData);
            }
            //返回每一项的json的列表
            List<String> jsonList = new ArrayList<>();
            for (List<Object> row : dataList) {
                JSONObject jsonObject = new JSONObject(true); // 使用 TreeMap 保持顺序
                for (int i = 0; i < header.size(); i++) {
                    Object value = row.get(i);
                    if(value == null){
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

    private static Object getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return null;
            default:
                return null;
        }
    }
}
