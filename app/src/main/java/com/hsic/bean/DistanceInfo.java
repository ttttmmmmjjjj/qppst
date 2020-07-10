package com.hsic.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2020/6/23.
 */

public class DistanceInfo implements Serializable {
    /// 距离类型
    private String DistanceType;
    /// 距离名称
    private String DistanceName;

    public String getDistanceType() {
        return DistanceType;
    }

    public void setDistanceType(String distanceType) {
        DistanceType = distanceType;
    }

    public String getDistanceName() {
        return DistanceName;
    }

    public void setDistanceName(String distanceName) {
        DistanceName = distanceName;
    }
}
