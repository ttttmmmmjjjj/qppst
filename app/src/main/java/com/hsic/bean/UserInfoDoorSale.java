package com.hsic.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/8/1.
 */

public class UserInfoDoorSale {
    private String userID;
    private String saleID;
    private String stationCode;//所属站点
    private String stationName;//所属站点
    private String userName;//用户姓名
    private String phoneNumber;//用户电话
    private String userType;//用户类型
    private String address;//用户地址
    private List<GoodsInfo> goodsInfo;//商品详细信息
    private String payMode;//付款方式
    private String operatorID;//操作人
    private String operatorName;//操作人
    private String emptyNO;//空瓶号
    private String fullNO;//满瓶号
    private String operationTime;
    private String payQRCode;
    private String totalPrice;//总价
    private String saleStatus;//订单状态(作废) 1:下单 ，2:提货 ，3:作废
    private String cansel_reason;//订单作废原因
    private String  order_need_receipt;//是否需要发票，为空表示不需要发票，不为空则是发票的抬头
    /// <summary>
    /// 总价说明
    /// </summary>
    private  String totalPriceInfo;


    /// <summary>
    /// 手输回收气瓶
    /// </summary>
    private String ReceiveQPByhand;

    /// <summary>
    /// 手输发瓶
    /// </summary>
    private String SendQPByhand;

    public String getTotalPriceInfo() {
        return totalPriceInfo;
    }

    public void setTotalPriceInfo(String totalPriceInfo) {
        this.totalPriceInfo = totalPriceInfo;
    }

    public String getReceiveQPByhand() {
        return ReceiveQPByhand;
    }

    public void setReceiveQPByhand(String receiveQPByhand) {
        ReceiveQPByhand = receiveQPByhand;
    }

    public String getSendQPByhand() {
        return SendQPByhand;
    }

    public void setSendQPByhand(String sendQPByhand) {
        SendQPByhand = sendQPByhand;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getSaleID() {
        return saleID;
    }

    public void setSaleID(String saleID) {
        this.saleID = saleID;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<GoodsInfo> getGoodsInfo() {
        return goodsInfo;
    }

    public void setGoodsInfo(List<GoodsInfo> goodsInfo) {
        this.goodsInfo = goodsInfo;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String getOperatorID() {
        return operatorID;
    }

    public void setOperatorID(String operatorID) {
        this.operatorID = operatorID;
    }

    public String getEmptyNO() {
        return emptyNO;
    }

    public void setEmptyNO(String emptyNO) {
        this.emptyNO = emptyNO;
    }

    public String getFullNO() {
        return fullNO;
    }

    public void setFullNO(String fullNO) {
        this.fullNO = fullNO;
    }

    public String getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(String operationTime) {
        this.operationTime = operationTime;
    }

    public String getPayQRCode() {
        return payQRCode;
    }

    public void setPayQRCode(String payQRCode) {
        this.payQRCode = payQRCode;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getSaleStatus() {
        return saleStatus;
    }

    public void setSaleStatus(String saleStatus) {
        this.saleStatus = saleStatus;
    }

    public String getCansel_reason() {
        return cansel_reason;
    }

    public void setCansel_reason(String cansel_reason) {
        this.cansel_reason = cansel_reason;
    }

    public String getOrder_need_receipt() {
        return order_need_receipt;
    }

    public void setOrder_need_receipt(String order_need_receipt) {
        this.order_need_receipt = order_need_receipt;
    }

    public class GoodsInfo {
        private String goodsType;//商品类型 1.气瓶，2.配件
        private String goodsCount;//商品数量
        private String unitPrice;//商品单价
        private String goodsName;
        private String goodsCode;
        private boolean isCheck;

        public boolean isCheck() {
            return isCheck;
        }

        public void setCheck(boolean check) {
            isCheck = check;
        }

        public String getGoodsCode() {
            return goodsCode;
        }

        public void setGoodsCode(String goodsCode) {
            this.goodsCode = goodsCode;
        }

        public String getGoodsType() {
            return goodsType;
        }

        public void setGoodsType(String goodsType) {
            this.goodsType = goodsType;
        }

        public String getGoodsCount() {
            return goodsCount;
        }

        public void setGoodsCount(String goodsCount) {
            this.goodsCount = goodsCount;
        }

        public String getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(String unitPrice) {
            this.unitPrice = unitPrice;
        }

        public String getGoodsName() {
            return goodsName;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
}
