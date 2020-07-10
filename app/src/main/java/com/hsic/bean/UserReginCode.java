package com.hsic.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/8/13.
 */

public class UserReginCode implements Serializable {
    private String userRegionCode;
    private String qpType;
    private String qpName;
    private String sendQPByInput;//手输发出去的瓶
    private String receiveQPByInput;//手输回收的瓶

    public String getQpName() {
        return qpName;
    }

    public void setQpName(String qpName) {
        this.qpName = qpName;
    }

    public String getQpType() {
        return qpType;
    }

    public void setQpType(String qpType) {
        this.qpType = qpType;
    }

    public String getUserRegionCode() {
        return userRegionCode;
    }

    public void setUserRegionCode(String userRegionCode) {
        this.userRegionCode = userRegionCode;
    }

    public String getSendQPByInput() {
        return sendQPByInput;
    }

    public void setSendQPByInput(String sendQPByInput) {
        this.sendQPByInput = sendQPByInput;
    }

    public String getReceiveQPByInput() {
        return receiveQPByInput;
    }

    public void setReceiveQPByInput(String receiveQPByInput) {
        this.receiveQPByInput = receiveQPByInput;
    }
}
