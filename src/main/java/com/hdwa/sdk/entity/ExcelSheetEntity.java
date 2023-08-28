package com.hdwa.sdk.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author abao
 * @since 2023/8/16
 * excel工作簿实体类
 */
@Data
public class ExcelSheetEntity {

    /**
     * 标题单元格列表
     */
    private List<String> titleColList = new ArrayList<>();

    /**
     * 内容列表
     */
    private List<List<String>> contentList = new ArrayList<>();
}
