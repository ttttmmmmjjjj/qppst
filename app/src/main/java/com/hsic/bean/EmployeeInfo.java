package com.hsic.bean;

/**
 * Created by Administrator on 2019/2/25.
 */

public class EmployeeInfo {
    private int ID;// 员工编号
    private String UserID;// 账号
    private String Password;// 密码 MD5加密
    private String UserName;// 用户姓名
    private String UserCardID;// 员工卡号 暂无用，可为空
    private String Telphone;// 联系方式
    private String Station;// 所属站点
    private String  UserType;// 员工类型 0：配送员工，1：门售员工，2：站点管理员，9：超级管理员
    private String CheckMan;// 审核人
    private String CheckTime;// 审核时间
    private int iState; // 状态标识位 0：未审核，1：已审核，7：审核不通过，9：停用
    private String PrePassword;// 用户的原密
    private String StationName;
    //新增查询日期字段
    private String SearchDate;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserCardID() {
        return UserCardID;
    }

    public void setUserCardID(String userCardID) {
        UserCardID = userCardID;
    }

    public String getTelphone() {
        return Telphone;
    }

    public void setTelphone(String telphone) {
        Telphone = telphone;
    }

    public String getStation() {
        return Station;
    }

    public void setStation(String station) {
        Station = station;
    }

    public String getUserType() {
        return UserType;
    }

    public void setUserType(String userType) {
        UserType = userType;
    }


    public String getCheckMan() {
        return CheckMan;
    }

    public void setCheckMan(String checkMan) {
        CheckMan = checkMan;
    }

    public String getCheckTime() {
        return CheckTime;
    }

    public void setCheckTime(String checkTime) {
        CheckTime = checkTime;
    }

    public int getiState() {
        return iState;
    }

    public void setiState(int iState) {
        this.iState = iState;
    }

    public String getPrePassword() {
        return PrePassword;
    }

    public void setPrePassword(String prePassword) {
        PrePassword = prePassword;
    }

    public String getSearchDate() {
        return SearchDate;
    }

    public void setSearchDate(String searchDate) {
        SearchDate = searchDate;
    }

    public String getStationName() {
        return StationName;
    }

    public void setStationName(String stationName) {
        StationName = stationName;
    }
}
