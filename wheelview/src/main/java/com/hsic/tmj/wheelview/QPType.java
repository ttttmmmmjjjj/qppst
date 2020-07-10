package com.hsic.tmj.wheelview;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/3/12.
 */

public class QPType implements Serializable {
    private  String QPType;
    private String QPName;
    private String QPNum;
    public String getQPType() {
        return QPType;
    }

    public void setQPType(String QPType) {
        this.QPType = QPType;
    }

    public String getQPName() {
        return QPName;
    }

    public void setQPName(String QPName) {
        this.QPName = QPName;
    }

    public String getQPNum() {
        return QPNum;
    }

    public void setQPNum(String QPNum) {
        this.QPNum = QPNum;
    }
}
