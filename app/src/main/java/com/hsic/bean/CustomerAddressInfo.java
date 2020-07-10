package com.hsic.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2020/6/23.
 */

public class CustomerAddressInfo implements Serializable {
    private String CustomerID;// 所属ID CustomerInfo中的CustomerID
    private String Address;// 地址
    private String FloorType;// 楼层类型 Floor1到Floor7
    private String DistanceType;// 配送距离类型 Distance1到Distance3
    private String LastQPApply;// 上次申请送气
    private int iState;// 状态标识位 0：未审核，1：已审核，9：停用
    private String Logtime;// 注册日期
    private int ID;// 地址编号
    private String AreaCode;// 所属区域代码
    /// 所属配送员
    private String BelongPeisong;
    /// EmployeeInfo中的UserType
    private int BelongPeisongType;
    // EmployeeInfo中的station
    private String Station;

    public String getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(String customerID) {
        CustomerID = customerID;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getFloorType() {
        return FloorType;
    }

    public void setFloorType(String floorType) {
        FloorType = floorType;
    }

    public String getDistanceType() {
        return DistanceType;
    }

    public void setDistanceType(String distanceType) {
        DistanceType = distanceType;
    }

    public String getLastQPApply() {
        return LastQPApply;
    }

    public void setLastQPApply(String lastQPApply) {
        LastQPApply = lastQPApply;
    }

    public int getiState() {
        return iState;
    }

    public void setiState(int iState) {
        this.iState = iState;
    }

    public String getLogtime() {
        return Logtime;
    }

    public void setLogtime(String logtime) {
        Logtime = logtime;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getAreaCode() {
        return AreaCode;
    }

    public void setAreaCode(String areaCode) {
        AreaCode = areaCode;
    }

    public String getBelongPeisong() {
        return BelongPeisong;
    }

    public void setBelongPeisong(String belongPeisong) {
        BelongPeisong = belongPeisong;
    }

    public int getBelongPeisongType() {
        return BelongPeisongType;
    }

    public void setBelongPeisongType(int belongPeisongType) {
        BelongPeisongType = belongPeisongType;
    }

    public String getStation() {
        return Station;
    }

    public void setStation(String station) {
        Station = station;
    }
}
