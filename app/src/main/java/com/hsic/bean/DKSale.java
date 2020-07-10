package com.hsic.bean;

/**
 * Created by tmj on 2019/7/3.
 */

public class DKSale {
    /// <summary>
    /// 所属客户号
    /// </summary>
    private String CustomerID;

    /// <summary>
    /// 代开户销售单号
    /// </summary>
    private String DKSaleID;


    /// <summary>
    /// 所属站点号
    /// </summary>
    private String StationID;

    /// <summary>
    /// 创建时间
    /// </summary>
    private String CreateTime;

    /// <summary>
    /// 退单人
    /// </summary>
    private String BackEmployeeID;

    /// <summary>
    /// 完成时间
    /// </summary>
    private String FinishTime;

    /// <summary>
    /// 退单原因
    /// </summary>
    private String BackInfo;
    /// <summary>
    /// 安检项描述
    /// </summary>
    private String InspectionItem;

    /// <summary>
    /// 客户电话
    /// </summary>
    private String telphone;

    /// <summary>
    /// 客户名称
    /// </summary>
    private String CustomerName;

    /// <summary>
    /// 所属站点名称
    /// </summary>
    private String StationName;
    /// <summary>
    /// 客户地址
    /// </summary>
    private String Address;

    //===============安检信息============================//

    /// <summary>
    /// 安检状态 0-不存在隐患 1-存在一般隐患 2-存在严重隐患
    /// </summary>
    private String InspectionStatus;

    /// <summary>
    /// 严重隐患 - 调压器泄漏 1-存在 0-不存在
    /// </summary>
    private String StopSupplyType1;

    /// <summary>
    /// 严重隐患 - 热水器无烟道或烟道安装不规范 1-存在 0-不存在
    /// </summary>
    private String StopSupplyType2;

    /// <summary>
    /// 严重隐患 - 使用场所不规范 1-存在 0-不存在
    /// </summary>
    private String StopSupplyType3;

    /// <summary>
    /// 严重隐患 - 未按规定安装使用泄漏报警器 1-存在 0-不存在
    /// </summary>
    private String StopSupplyType4;

    /// <summary>
    /// 严重隐患 - 调压器使用不规范 1-存在 0-不存在
    /// </summary>
    private String StopSupplyType5;

    /// <summary>
    /// 严重隐患 - 商业餐饮用户未按规定设瓶组间或无瓶库 1-存在 0-不存在
    /// </summary>
    private String StopSupplyType6;

    /// <summary>
    /// 严重隐患 - 同室使用二种及以上气源 1-存在 0-不存在
    /// </summary>
    private String StopSupplyType7;

    /// <summary>
    /// 严重隐患 - 其他严重隐患 1-存在 0-不存在
    /// </summary>
    private String StopSupplyType8;

    /// <summary>
    /// 一般隐患 - 使用非强制排风热水器 1-存在 0-不存在
    /// </summary>
    private String UnInstallType1;

    /// <summary>
    /// 一般隐患 - 热水器烟道未通至户外(已取消) 1-存在 0-不存在
    /// </summary>
    private String UnInstallType2;

    /// <summary>
    /// 一般隐患 - 使用非安全型灶具 1-存在 0-不存在
    /// </summary>
    private String UnInstallType3;

    /// <summary>
    /// 一般隐患 - 使用场所排风不畅 1-存在 0-不存在
    /// </summary>
    private String UnInstallType4;

    /// <summary>
    /// 一般隐患 - 调压器老化(已取消) 1-存在 0-不存在
    /// </summary>
    private String UnInstallType5;

    /// <summary>
    /// 一般隐患 - 橡胶管使用不规范 1-存在 0-不存在
    /// </summary>
    private String UnInstallType6;

    /// <summary>
    /// 一般隐患 - 橡胶管老化(已取消) 1-存在 0-不存在
    /// </summary>
    private String UnInstallType7;

    /// <summary>
    /// 一般隐患 - 设施连接未装夹箍(已取消) 1-存在 0-不存在
    /// </summary>
    private String UnInstallType8;

    /// <summary>
    /// 一般隐患 - 燃气具故障 1-存在 0-不存在
    /// </summary>
    private String UnInstallType9;

    /// <summary>
    /// 一般隐患 - 强制使用液化气(已取消) 1-存在 0-不存在
    /// </summary>
    private String UnInstallType10;

    /// <summary>
    /// 一般隐患 - 餐饮用户灶位排烟设施不全 1-存在 0-不存在
    /// </summary>
    private String UnInstallType11;

    /// <summary>
    /// 一般隐患 - 其他隐患 1-存在 0-不存在
    /// </summary>
    private String UnInstallType12;

    /// <summary>
    /// 操作人编号
    /// </summary>
    private String InspectionMan;

    /// <summary>
    /// 关联ID
    /// </summary>
    private String AttachID;
    private String IsInspected;

    public String getStopSupplyType1() {
        return StopSupplyType1;
    }

    public void setStopSupplyType1(String stopSupplyType1) {
        StopSupplyType1 = stopSupplyType1;
    }

    public String getStopSupplyType2() {
        return StopSupplyType2;
    }

    public void setStopSupplyType2(String stopSupplyType2) {
        StopSupplyType2 = stopSupplyType2;
    }

    public String getStopSupplyType3() {
        return StopSupplyType3;
    }

    public void setStopSupplyType3(String stopSupplyType3) {
        StopSupplyType3 = stopSupplyType3;
    }

    public String getStopSupplyType4() {
        return StopSupplyType4;
    }

    public void setStopSupplyType4(String stopSupplyType4) {
        StopSupplyType4 = stopSupplyType4;
    }

    public String getStopSupplyType5() {
        return StopSupplyType5;
    }

    public void setStopSupplyType5(String stopSupplyType5) {
        StopSupplyType5 = stopSupplyType5;
    }

    public String getStopSupplyType6() {
        return StopSupplyType6;
    }

    public void setStopSupplyType6(String stopSupplyType6) {
        StopSupplyType6 = stopSupplyType6;
    }

    public String getStopSupplyType7() {
        return StopSupplyType7;
    }

    public void setStopSupplyType7(String stopSupplyType7) {
        StopSupplyType7 = stopSupplyType7;
    }

    public String getStopSupplyType8() {
        return StopSupplyType8;
    }

    public void setStopSupplyType8(String stopSupplyType8) {
        StopSupplyType8 = stopSupplyType8;
    }

    public String getUnInstallType1() {
        return UnInstallType1;
    }

    public void setUnInstallType1(String unInstallType1) {
        UnInstallType1 = unInstallType1;
    }

    public String getUnInstallType2() {
        return UnInstallType2;
    }

    public void setUnInstallType2(String unInstallType2) {
        UnInstallType2 = unInstallType2;
    }

    public String getUnInstallType3() {
        return UnInstallType3;
    }

    public void setUnInstallType3(String unInstallType3) {
        UnInstallType3 = unInstallType3;
    }

    public String getUnInstallType4() {
        return UnInstallType4;
    }

    public void setUnInstallType4(String unInstallType4) {
        UnInstallType4 = unInstallType4;
    }

    public String getUnInstallType5() {
        return UnInstallType5;
    }

    public void setUnInstallType5(String unInstallType5) {
        UnInstallType5 = unInstallType5;
    }

    public String getUnInstallType6() {
        return UnInstallType6;
    }

    public void setUnInstallType6(String unInstallType6) {
        UnInstallType6 = unInstallType6;
    }

    public String getUnInstallType7() {
        return UnInstallType7;
    }

    public void setUnInstallType7(String unInstallType7) {
        UnInstallType7 = unInstallType7;
    }

    public String getUnInstallType8() {
        return UnInstallType8;
    }

    public void setUnInstallType8(String unInstallType8) {
        UnInstallType8 = unInstallType8;
    }

    public String getUnInstallType9() {
        return UnInstallType9;
    }

    public void setUnInstallType9(String unInstallType9) {
        UnInstallType9 = unInstallType9;
    }

    public String getUnInstallType10() {
        return UnInstallType10;
    }

    public void setUnInstallType10(String unInstallType10) {
        UnInstallType10 = unInstallType10;
    }

    public String getUnInstallType11() {
        return UnInstallType11;
    }

    public void setUnInstallType11(String unInstallType11) {
        UnInstallType11 = unInstallType11;
    }

    public String getUnInstallType12() {
        return UnInstallType12;
    }

    public void setUnInstallType12(String unInstallType12) {
        UnInstallType12 = unInstallType12;
    }

    public String getInspectionMan() {
        return InspectionMan;
    }

    public void setInspectionMan(String inspectionMan) {
        InspectionMan = inspectionMan;
    }

    public String getAttachID() {
        return AttachID;
    }

    public void setAttachID(String attachID) {
        AttachID = attachID;
    }

    public String getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(String customerID) {
        CustomerID = customerID;
    }

    public String getDKSaleID() {
        return DKSaleID;
    }

    public void setDKSaleID(String DKSaleID) {
        this.DKSaleID = DKSaleID;
    }

    public String getStationID() {
        return StationID;
    }

    public void setStationID(String stationID) {
        StationID = stationID;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getBackEmployeeID() {
        return BackEmployeeID;
    }

    public void setBackEmployeeID(String backEmployeeID) {
        BackEmployeeID = backEmployeeID;
    }

    public String getFinishTime() {
        return FinishTime;
    }

    public void setFinishTime(String finishTime) {
        FinishTime = finishTime;
    }

    public String getBackInfo() {
        return BackInfo;
    }

    public void setBackInfo(String backInfo) {
        BackInfo = backInfo;
    }

    public String getInspectionStatus() {
        return InspectionStatus;
    }

    public void setInspectionStatus(String inspectionStatus) {
        InspectionStatus = inspectionStatus;
    }

    public String getInspectionItem() {
        return InspectionItem;
    }

    public void setInspectionItem(String inspectionItem) {
        InspectionItem = inspectionItem;
    }

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getStationName() {
        return StationName;
    }

    public void setStationName(String stationName) {
        StationName = stationName;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getIsInspected() {
        return IsInspected;
    }

    public void setIsInspected(String isInspected) {
        IsInspected = isInspected;
    }
}
