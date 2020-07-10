package com.hsic.bean;


import java.util.List;

/**
 * Created by Administrator on 2019/2/26.
 */

public class SaleAll {
    private Sale Sale;// 销售信息
    private UserXJInfo UserXJInfo;//用户相关信息
    private List<ScanHistory> ScanHistory; // 发瓶收瓶列表
    public boolean isUpdate=false;
    private CustomerInfo CustomerInfo;//
    /// <summary>
    /// 客户未归还气瓶信息
    /// </summary>
    private List<UserQPInfo> UserQPInfo_lsit;

    public List<UserQPInfo> getUserQPInfo_lsit() {
        return UserQPInfo_lsit;
    }

    public void setUserQPInfo_lsit(List<UserQPInfo> userQPInfo_lsit) {
        UserQPInfo_lsit = userQPInfo_lsit;
    }

    public CustomerInfo getCustomerInfo() {
        return CustomerInfo;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        CustomerInfo = customerInfo;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public Sale getSale() {
        return Sale;
    }

    public void setSale(Sale sale) {
        Sale = sale;
    }

    public UserXJInfo getUserXJInfo() {
        return UserXJInfo;
    }

    public void setUserXJInfo(UserXJInfo userXJInfo) {
        UserXJInfo = userXJInfo;
    }

    public List<ScanHistory> getScanHistory() {
        return ScanHistory;
    }

    public void setScanHistory(List<ScanHistory> scanHistory) {
        ScanHistory = scanHistory;
    }
}
