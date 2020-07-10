package com.hsic.bean;

/**
 * Created by Administrator on 2020/6/30.
 */

public class UserQPInfo {
    /// <summary>
    /// 客户号
    /// </summary>
    private  String CustomerID;

    /// <summary>
    /// 瓶号
    /// </summary>
    private String QPNO;

    public String getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(String customerID) {
        CustomerID = customerID;
    }

    public String getQPNO() {
        return QPNO;
    }

    public void setQPNO(String QPNO) {
        this.QPNO = QPNO;
    }
}
