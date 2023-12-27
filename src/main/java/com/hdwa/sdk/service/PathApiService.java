package com.hdwa.sdk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.entity.PathApiParam;
import com.hdwa.sdk.entity.repository.DataContainer;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.utils.CalculateApiJsonUtil;
import com.hdwa.sdk.utils.FilterUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author abao
 * @since 2023/8/2
 * 路径接口方式查询服务
 */
@Slf4j
@Service
public class PathApiService {

    /**
     * 路径查询
     *
     * @param param
     * @return
     */
    public Object post(PathApiParam param) {
        JSONArray valuePath = param.getPath();
        try {
            RepositoryImpl repository = DataContainer.projectMap.get(BaseDecConstant.CURRENT_PROJECT_ID);
            if (repository == null) {
                return "null";
            }
            if (valuePath.size() == 0) {
                return repository.objectData.toJSON(1);
            }
            Object valueObject = CalculateApiJsonUtil.getValueObject(repository, valuePath);

            return CalculateApiJsonUtil.getValueJson(valueObject);
        } catch (Exception e) {
            log.error("按路径查询接口出现异常：" + valuePath, e);
            throw e;
        }
    }


    /**
     * 路径查询分页
     *
     * @param param
     * @return
     */
    public Object postPage(PathApiParam param) {
        try {
            RepositoryImpl repository = DataContainer.projectMap.get(BaseDecConstant.CURRENT_PROJECT_ID);
            if (repository == null) {
                return "null";
            }
            return FilterUtil.postPage(repository, (JSONObject) JSON.toJSON(param));
        } catch (Exception e) {
            log.error("按路径查询分页接口出现异常：" + param.getPath(), e);
            throw e;
        }
    }

    /**
     * 数据筛选数据导出
     *
     * @param param
     * @return
     */
    public void postExport(PathApiParam param, HttpServletRequest request, HttpServletResponse response) {
        try {
            RepositoryImpl repository = DataContainer.projectMap.get(BaseDecConstant.CURRENT_PROJECT_ID);
            if (repository == null) {
                return;
            }
            //点位数据
            Object valueObject = CalculateApiJsonUtil.getValueObject(repository, param.getPointPath());
            JSONArray pointArray = new JSONArray();
            //加入默认的列
            pointArray.addAll(BaseDecConstant.BASE_HEADER);
            pointArray.addAll((JSONArray) CalculateApiJsonUtil.getValueJson(valueObject));

            //对象数据
            param.setPageIndex(0);
            param.setPageSize(10000);
            JSONObject jsonObject = FilterUtil.postPage(repository, (JSONObject) JSON.toJSON(param));
            JSONArray jsonDataArray = jsonObject.getJSONArray(BaseDecConstant.CONTENT2);

            Workbook workbook = new XSSFWorkbook();
            // 创建一个工作表
            Sheet sheet = workbook.createSheet(param.getClassName());
            Row headerRow = sheet.createRow(0);
            //设置行高
            headerRow.setHeightInPoints(40);
            setTitle(pointArray, headerRow, workbook, sheet);
            setRow(jsonDataArray, sheet, pointArray, workbook);

            // 获取当前时间
            LocalDateTime currentTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            // 设置响应头
            String fileName = param.getClassName() + "-" + currentTime.format(formatter) + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            // 获取输出流，将文件内容写入响应
            try (FileOutputStream ignored = new FileOutputStream(fileName)) {
                // 将工作簿写入输出流
                //workbook.write(response.getOutputStream());

                //下载到本地测试使用
                workbook.write(ignored);
            }
        } catch (Exception e) {
            log.error("数据筛选数据导出出现异常：" + param.getPath(), e);
        }

    }


    /**
     * 标题行
     *
     * @param pointArray
     * @param headerRow
     */
    public void setTitle(JSONArray pointArray, Row headerRow, Workbook workbook, Sheet sheet) {
        int colNum = 0;
        for (int i = 0; i < pointArray.size(); i++) {
            //设置列宽
            sheet.setColumnWidth(i, 25 * 256);
            JSONObject jsonObject = (JSONObject) pointArray.get(i);
            String columnName = (String) jsonObject.get(BaseDecConstant.NAME);
            Cell headerCell = headerRow.createCell(colNum);
            headerCell.setCellValue(columnName);
            // 创建样式
            CellStyle titleCellStyle = workbook.createCellStyle();
            // 设置样式属性，例如字体、颜色、对齐等
            titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
            titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleCellStyle.setFont(titleFont);
            // 应用样式
            headerCell.setCellStyle(titleCellStyle);
            colNum++;
        }
    }

    /**
     * 数据行
     *
     * @param jsonDataArray
     * @param sheet
     * @param pointArray
     */
    public void setRow(JSONArray jsonDataArray, Sheet sheet, JSONArray pointArray, Workbook workbook) {
        // 遍历数据行 JSONArray
        for (int i = 0; i < jsonDataArray.size(); i++) {
            JSONObject jsonDataObject = (JSONObject) jsonDataArray.get(i);
            jsonDataObject.put("number", i + 1);
            // 创建数据行
            Row dataRow = sheet.createRow(i + 1);
            // 遍历表头行的 code 值，匹配数据行的属性名
            for (int j = 0; j < pointArray.size(); j++) {
                JSONObject headerObject = (JSONObject) pointArray.get(j);
                String codeInHeader = headerObject.getString("code");
                String dataType = headerObject.getString("dataType");
                // 根据表头中的 code 值在数据行中查找对应的数据
                Object cellValue = jsonDataObject.get(codeInHeader);
                // 创建数据单元格
                Cell dataCell = dataRow.createCell(j);
                // 根据属性类型设置数据
                if (cellValue instanceof String) {
                    //楼栋/楼层需要拼接
                    if (codeInHeader.equals("buildingName")) {
                        dataCell.setCellValue(cellValue + "/" + jsonDataObject.getString("floorName"));
                    } else {
                        dataCell.setCellValue((String) cellValue);
                    }
                } else if (cellValue instanceof Number) {
                    Number value = (Number) cellValue;
                    //点位是枚举类型需要回显示中文
                    if (dataType.equals("BOOLEAN") || dataType.equals("ENUM")) {
                        JSONArray dataSource = headerObject.getJSONArray("dataSource");
                        for (Object dataSourceObj : dataSource) {
                            JSONObject dataSourceJObj = (JSONObject) dataSourceObj;
                            if (dataSourceJObj.getIntValue("code") == (value.intValue())) {
                                dataCell.setCellValue(dataSourceJObj.getString("name"));
                                break;
                            }
                        }
                    } else {
                        // 创建一个数据格式对象，设置为两位小数
                        if (cellValue instanceof Double) {
                            DataFormat dataFormat = workbook.createDataFormat();
                            CellStyle cellStyle = workbook.createCellStyle();
                            cellStyle.setDataFormat(dataFormat.getFormat("0.00"));
                            dataCell.setCellStyle(cellStyle);

                            dataCell.setCellValue(value.doubleValue());
                        } else {
                            dataCell.setCellValue(value.intValue());
                        }
                    }
                }
                // 创建样式
                CellStyle titleCellStyle = workbook.createCellStyle();
                // 设置样式属性，例如字体、颜色、对齐等
                titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
                titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                //字体
                Font titleFont = workbook.createFont();
                titleFont.setFontHeightInPoints((short) 12);
                titleCellStyle.setFont(titleFont);
                // 应用样式
                dataCell.setCellStyle(titleCellStyle);
            }
        }
    }

}
