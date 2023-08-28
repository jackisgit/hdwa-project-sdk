package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONReader;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * @author abao
 * @since 2023/8/17
 * 读取文件工具类
 */
public class ReadFileUtil {

    /**
     * 读取jsonArray文件内容
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static JSONArray readJsonArray(File file) throws Exception {
        Object result = readJson(file);
        FastJsonUtil.Normalize(result);
        return (JSONArray) result;
    }

    /**
     * 读取.json文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static Object readJson(File file) throws Exception {
        Object result;
        InputStream is = Files.newInputStream(file.toPath());
        Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
        JSONReader json_reader = new JSONReader(reader);
        result = json_reader.readObject();
        json_reader.close();
        reader.close();
        is.close();
        return result;
    }
}
