package com.hdwa.control.client;

import com.hdwa.control.constant.RequestUrlConstant;
import com.hdwa.control.entity.BatchPointSetParam;
import com.hdwa.control.entity.BatchPointSetResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @description: 控制程序（指令下发）
 * @since: 2023/8/17
 * @version: V4.0
 */
@FeignClient(name = "iot-collect", url = "${iot.project.url:127.0.0.1:8852}")
public interface EmsControlClient {

    /**
     * 控制指令下发
     *
     * @param batchPointSetParam
     * @return
     */
    @PostMapping(value = RequestUrlConstant.POINT_SET_BATCH_POST)
    BatchPointSetResult pointSetBatch(@RequestBody BatchPointSetParam batchPointSetParam);

}