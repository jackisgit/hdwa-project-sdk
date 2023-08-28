package com.hdwa.sdk.entity.repository;


import com.hdwa.sdk.entity.scene.SceneDataValue;

import java.util.Date;

public class WaitItem {
    public SceneDataValue sdv;
    public Date time;

    public WaitItem(SceneDataValue sdv, Date time) {
        this.sdv = sdv;
        this.time = time;
    }
}
