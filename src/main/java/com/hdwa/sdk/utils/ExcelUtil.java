package com.hdwa.sdk.utils;

import com.hdwa.sdk.entity.ExcelSheetEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author abao
 * @since 2023/8/16
 * excel文件工具类
 */
@Slf4j
public class ExcelUtil {

    public static Map<String, ExcelSheetEntity> readExcel(InputStream stream) throws Exception {
        Map<String, ExcelSheetEntity> map = new HashMap<>(16);
        Workbook workbook = WorkbookFactory.create(stream);
        workbook.forEach(sheet -> {
            map.put(sheet.getSheetName(), getTitleCol(sheet));
        });
        workbook.close();
        return map;
    }

    /**
     * 解析
     *
     * @param sheet
     * @return
     */
    public static ExcelSheetEntity getTitleCol(Sheet sheet) {
        ExcelSheetEntity excelSheetEntity = new ExcelSheetEntity();
        int rowStartNum = 0;
        Row sheetRow = sheet.getRow(rowStartNum);

        //标题
        for (int i = 0; i <= sheetRow.getLastCellNum(); i++) {
            Cell cell = sheetRow.getCell(i);
            String value = null;
            if (cell != null) {
                String cellValue = getCellValue(cell);
                if (cellValue != null && cellValue.trim().length() > 0) {
                    value = cellValue.trim();
                }
            }
            excelSheetEntity.getTitleColList().add(value);
        }

        //内容
        for (int i = rowStartNum + 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            List<String> contentList = new ArrayList<>();
            if (row != null) {
                for (int index_col = 0; index_col < excelSheetEntity.getTitleColList().size(); index_col++) {
                    Cell cell = row.getCell(index_col);
                    String content = null;
                    if (cell != null) {
                        String cellValue = getCellValue(cell);
                        if (cellValue != null && cellValue.trim().length() > 0) {
                            content = cellValue.trim();
                        }
                    }
                    contentList.add(content);
                }
            }
            excelSheetEntity.getContentList().add(contentList);
        }

        return excelSheetEntity;
    }


    /**
     * 获取单元格内容
     *
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        String value = null;
        switch (cell.getCellType()) {
            case NUMERIC:
            case FORMULA:
                try {
                    value = "" + cell.getNumericCellValue();
                } catch (Exception e) {
                    log.error("*****单元格转换number错误：" + cell.getSheet().getSheetName() + "--" + (cell.getRowIndex()+1) + "行--" + (cell.getColumnIndex()+1) + "列----值：" + cell);
                }
                break;
            case STRING:
                try {
                    value = cell.getStringCellValue();
                } catch (Exception e) {
                    log.error("*****单元格转换string错误：" + cell.getSheet().getSheetName() + "--" + (cell.getRowIndex()+1) + "行--" + (cell.getColumnIndex()+1) + "列----值：" + cell);
                }
                break;
            default:
        }
        return value;
    }
}
