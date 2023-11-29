package com.hdwa.sdk.interceptor;

/**
 * @author abao
 * @since 2023/3/15
 */

import com.alibaba.fastjson.JSONObject;

public enum ResultEnum {

    TOKEN_EXCEPTION(401, "token未携带或认证失败，请重新登陆"),
    TOKEN_EXCEPTION_ERROR(401, "token已过期或验证失败，请重新登陆");

    private int code;

    private String msg;

    ResultEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        return jsonObject.toString();
    }

}