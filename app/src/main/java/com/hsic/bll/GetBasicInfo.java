package com.hsic.bll;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2019/2/25.
 */

public class GetBasicInfo {
    private Context context = null;
    SharedPreferences deviceSetting;

    public GetBasicInfo(Context context) {
        this.context = context;
        deviceSetting = context.getSharedPreferences("DeviceSetting", 0);
    }

    public String getStationID() {
        String ret = "";
        ret = deviceSetting.getString("StationID", "");//
        return ret;
    }
    public String getOperationType() {
        String ret = "";
        ret = deviceSetting.getString("OperationType", "");//
        return ret;
    }
    public String getDeviceID() {
        String ret = "";
        ret = deviceSetting.getString("DeviceID", "");// �豸���
        return ret;
    }
    public String getDeviceType() {
        String ret = "";
        ret = deviceSetting.getString("DeviceType", "");//
        return ret;
    }
    public String getStationName() {
        String ret = "";
        ret = deviceSetting.getString("StationName", "");//
        return ret;
    }

    public String getOperationID() {
        String ret = "";
        ret = deviceSetting.getString("OperationID", "");//
        return ret;
    }

    public String getOperationName() {
        String ret = "";
        ret = deviceSetting.getString("OperationName", "");//
        return ret;
    }

    public String getBlueToothAdd() {
        String ret = "";
        ret = deviceSetting.getString("BlueToothAdd", "");// ַ
        return ret;
    }

    public String getStuffTagID() {
        String ret = "";
        ret = deviceSetting.getString("StaffTagID", "");//
        return ret;
    }

    public String getRectifyMan() {
        String ret = "";
        ret = deviceSetting.getString("RectifyMan", "");//
        return ret;
    }
    public String getLoginMode() {
        String ret = "";
        ret = deviceSetting.getString("LoginMode", "");//
        return ret;
    }
    public String getLoginStuffMode() {
        String ret = "";
        ret = deviceSetting.getString("LoginStaffMode", "");//
        return ret;
    }
    public String getCompanyCode() {
        String ret = "";
        ret = deviceSetting.getString("CompanyCode", "");//
        return ret;
    }
    public String getCompanyName() {
        String ret = "";
        ret = deviceSetting.getString("CompanyName", "");//
        return ret;
    }
    public String getCompanyPhone() {
        String ret = "";
        ret = deviceSetting.getString("CompanyPhone", "");//
        return ret;
    }
    /**
     * 获取登录账户
     * @return
     */
    public String getAccount() {
        String ret = "";
        ret = deviceSetting.getString("Account", "");//
        return ret;
    }

    /**
     * 获取登录密码
     * @return
     */
    public String getPass() {
        String ret = "";
        ret = deviceSetting.getString("PassWord", "");//
        return ret;
    }

    /***
     * 手持机数据库版本
     * @return
     */
    public String getDBVersion() {
        String ret = "";
        ret = deviceSetting.getString("DBVersion", "");// ������Ա
        return ret;
    }

    /**
     *登录是否记住密码勾选框
     * @return
     */
    public boolean getIsChecked(){
        boolean ret = false;
        ret = deviceSetting.getBoolean("IsChecked", false);// ������Ա
        return ret;
    }
}
