package com.hsic.bean;

import java.util.List;

/**
 * Created by Administrator on 2019/2/25.
 */

public class LoginInfo {
    private EmployeeInfo EmployeeInfo;//登录人员信息
    private List<StreetInfo> StreetInfo;//街道信息
    private List<com.hsic.bean.CustomerTypeInfo> CustomerTypeInfo;//客户类型
    private List<CustomerInfo> CustomerInfo ;//客户信息
    private List<EmployeeInfo> EmployeeInfo_tmp ;//配送员工信息
    private List<StationInfo> StationInfo;//站点

    public com.hsic.bean.EmployeeInfo getEmployeeInfo() {
        return EmployeeInfo;
    }

    public void setEmployeeInfo(com.hsic.bean.EmployeeInfo employeeInfo) {
        EmployeeInfo = employeeInfo;
    }

    public List<com.hsic.bean.StreetInfo> getStreetInfo() {
        return StreetInfo;
    }

    public void setStreetInfo(List<com.hsic.bean.StreetInfo> streetInfo) {
        StreetInfo = streetInfo;
    }

    public List<com.hsic.bean.CustomerTypeInfo> getCustomerTypeInfo() {
        return CustomerTypeInfo;
    }

    public void setCustomerTypeInfo(List<com.hsic.bean.CustomerTypeInfo> customerTypeInfo) {
        CustomerTypeInfo = customerTypeInfo;
    }

    public List<com.hsic.bean.CustomerInfo> getCustomerInfo() {
        return CustomerInfo;
    }

    public void setCustomerInfo(List<com.hsic.bean.CustomerInfo> customerInfo) {
        CustomerInfo = customerInfo;
    }

    public List<com.hsic.bean.EmployeeInfo> getEmployeeInfo_tmp() {
        return EmployeeInfo_tmp;
    }

    public void setEmployeeInfo_tmp(List<com.hsic.bean.EmployeeInfo> employeeInfo_tmp) {
        EmployeeInfo_tmp = employeeInfo_tmp;
    }

    public List<com.hsic.bean.StationInfo> getStationInfo() {
        return StationInfo;
    }

    public void setStationInfo(List<com.hsic.bean.StationInfo> stationInfo) {
        StationInfo = stationInfo;
    }
}
