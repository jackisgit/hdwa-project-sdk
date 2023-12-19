package com.hdwa.sdk.service;

import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.constant.UrlConstant;
import com.hdwa.sdk.utils.OkHttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author abao
 * @since 2023/11/29
 * 验证token服务
 */
@Service
public class TokenService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate1;

    @Value("${url.monitor}")
    private String monitorUrl;

    /**
     * 验证token
     *
     * @param token
     * @return
     */
    public boolean verifyToken(String token) {
        String redisKey = BaseDecConstant.TOKEN + token;
        //先去redis查询token有效标识是否存在
        if (!Boolean.TRUE.equals(redisTemplate1.hasKey(redisKey))) {
            //验证token
            String res;
            try {
                res = OkHttpClientUtil.httpGet(monitorUrl + UrlConstant.VERIFY_TOKEN_URL, token);
            } catch (Exception e) {
                return false;
            }
            //验证通过存储到redis，设置过期时间
            if (BaseDecConstant.OK.equals(res)) {
                redisTemplate1.opsForValue().set(redisKey, token, BaseDecConstant.TOKEN_TIME_OUT, TimeUnit.SECONDS);
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

}
