package com.hsic.bean;

/**
 * Created by Administrator on 2019/2/25.
 */

public class StreetInfo {
    /// 流水号
    private int ID;
    /// 区域代码
    private String AreaCode;
    private String QuCode;
    /// 区名称
    private String QuName;
    /// 街道名称
    private String JieName;
    private String JieCode;

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

    public String getQuName() {
        return QuName;
    }

    public void setQuName(String quName) {
        QuName = quName;
    }

    public String getJieName() {
        return JieName;
    }

    public void setJieName(String jieName) {
        JieName = jieName;
    }

    public String getQuCode() {
        return QuCode;
    }

    public void setQuCode(String quCode) {
        QuCode = quCode;
    }

    public String getJieCode() {
        return JieCode;
    }

    public void setJieCode(String jieCode) {
        JieCode = jieCode;
    }
}
