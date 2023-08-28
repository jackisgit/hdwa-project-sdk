package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.UrlConstant;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.http.MediaType;

import java.util.concurrent.TimeUnit;

/**
 * @author abao
 * @since 2023/8/15
 * http请求
 */
public class OkHttpClientUtil {

    /**
     * post 请求
     *
     * @param requestBody 请求参数
     * @param ur          请求路径
     * @return
     * @throws Exception
     */
    public static JSONObject httpPost(JSONObject requestBody, String ur) throws Exception {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.readTimeout(60, TimeUnit.SECONDS);
        OkHttpClient httpClient = builder.build();
        RequestBody res = RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON_VALUE), requestBody.toJSONString());
        // 创建 POST 请求
        Request request = new Request.Builder()
                .url(ur)
                .post(res)
                .header(UrlConstant.AUTHORIZATION, UrlConstant.TOKEN)
                .build();
        // 执行 POST 请求并接收响应
        Response response = httpClient.newCall(request).execute();
        //转换成jsonObject
        JSONObject jsonObject = JSONObject.parseObject(response.body().string());
        //关闭连接
        response.close();
        return jsonObject;
    }
}
