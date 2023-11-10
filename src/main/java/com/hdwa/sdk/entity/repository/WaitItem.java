package com.hdwa.sdk.entity.repository;


import com.hdwa.sdk.entity.scene.DataValue;

import java.util.Date;

/**
 * 等待元素定义
 */
public class WaitItem {
    public DataValue sdv;
    public Date time;

    public WaitItem(DataValue sdv, Date time) {
        this.sdv = sdv;
        this.time = time;
    }
}
