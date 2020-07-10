package com.hsic.bean;

/**
 * Created by Administrator on 2020/6/18.
 */

public class Price_QPQT {
    private String CustomerType;///// 客户类型
    private String CustomerTypeName;//客户类型描述
    private String Type;///// 气瓶种类
    private String Name;//气瓶描述
    private String QPPrice;//气瓶价格
    private String QTPrice;/// 气体价格
    private String iState;//1：正使用，9：停用
    private String IsEx;/// 状态标识位 0：不是附件，1：是附件
    private String count;
    public String getCustomerType() {
        return CustomerType;
    }

    public void setCustomerType(String customerType) {
        CustomerType = customerType;
    }

    public String getCustomerTypeName() {
        return CustomerTypeName;
    }

    public void setCustomerTypeName(String customerTypeName) {
        CustomerTypeName = customerTypeName;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getQPPrice() {
        return QPPrice;
    }

    public void setQPPrice(String QPPrice) {
        this.QPPrice = QPPrice;
    }

    public String getQTPrice() {
        return QTPrice;
    }

    public void setQTPrice(String QTPrice) {
        this.QTPrice = QTPrice;
    }

    public String getiState() {
        return iState;
    }

    public void setiState(String iState) {
        this.iState = iState;
    }

    public String getIsEx() {
        return IsEx;
    }

    public void setIsEx(String isEx) {
        IsEx = isEx;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
