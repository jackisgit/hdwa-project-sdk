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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;

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
    public void postExport(PathApiParam param) {
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
            param.setPageSize(10);
            JSONObject jsonObject = FilterUtil.postPage(repository, (JSONObject) JSON.toJSON(param));
            JSONArray jsonDataArray = jsonObject.getJSONArray(BaseDecConstant.CONTENT2);

            Workbook workbook = new XSSFWorkbook();
            // 创建一个工作表
            Sheet sheet = workbook.createSheet("实时数据");
            // 创建标题行
            Row headerRow = sheet.createRow(0);
            setTitle(pointArray, headerRow);

            // 导出到文件
            FileOutputStream fileOut = new FileOutputStream("test.xlsx");
            workbook.write(fileOut);

        } catch (Exception e) {
            log.error("数据筛选数据导出出现异常：" + param.getPath(), e);
        }

    }


    /**
     * 创建标题行
     *
     * @param pointArray
     * @param headerRow
     */
    public void setTitle(JSONArray pointArray, Row headerRow) {
        int colNum = 0;
        for (Object o : pointArray) {
            JSONObject jsonObject = (JSONObject) o;
            String columnName = (String) jsonObject.get(BaseDecConstant.NAME);
            Cell headerCell = headerRow.createCell(colNum);
            headerCell.setCellValue(columnName);
            colNum++;
        }
    }

    public void setRow(JSONArray jsonDataArray,Sheet sheet,JSONArray pointArray){
        // 遍历数据行 JSONArray
        for (int i = 0; i < jsonDataArray.size(); i++) {
            JSONObject jsonDataObject = (JSONObject) jsonDataArray.get(i);

            // 创建数据行
            Row dataRow = sheet.createRow(i + 1);

            // 遍历表头行的 code 值，匹配数据行的属性名
            for (int j = 0; j < pointArray.size(); j++) {
                JSONObject headerObject = (JSONObject) pointArray.get(j);
                String codeInHeader = headerObject.keySet().iterator().next();

                // 根据表头中的 code 值在数据行中查找对应的数据
                Object cellValue = jsonDataObject.get(headerObject.getString("code"));

                // 创建数据单元格
                Cell dataCell = dataRow.createCell(j);

                // 根据属性类型设置数据
                if (cellValue instanceof String) {
                    dataCell.setCellValue((String) cellValue);
                } else if (cellValue instanceof Number) {
                    dataCell.setCellValue(((Number) cellValue).doubleValue());
                } else {
                    // 其他类型的数据处理
                }
            }
        }
    }

}
