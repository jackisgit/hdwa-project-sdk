package com.hdwa.sdk.service;

import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.constant.UrlConstant;
import com.hdwa.sdk.utils.OkHttpClientUtil;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
public class TokenService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

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
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            //验证token
            String res;
            try {
                res = OkHttpClientUtil.httpGet(monitorUrl + UrlConstant.VERIFY_TOKEN_URL, token);
            } catch (Exception e) {
                return false;
            }
            //验证通过存储到redis，设置过期时间
            if (BaseDecConstant.OK.equals(res)) {
                redisTemplate.opsForValue().set(redisKey, token, BaseDecConstant.TOKEN_TIME_OUT, TimeUnit.SECONDS);
                return true;
            } else {
                log.error("***token验证失败：" + res);
                return false;
            }
        } else {
            return true;
        }
    }

}
