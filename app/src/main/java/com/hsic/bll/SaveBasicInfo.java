package com.hsic.bll;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2019/2/25.
 */

public class SaveBasicInfo {
    private Context context = null;
    SharedPreferences deviceSetting;
    SharedPreferences.Editor mEditor;
    public SaveBasicInfo(Context context){
        this.context=context;
        deviceSetting = context.getSharedPreferences("DeviceSetting", 0);
        mEditor= deviceSetting.edit();
    }

    /**
     * 设备编号
     * @param DeviceID
     */
    public void saveDeviceID(String DeviceID){
        mEditor.putString("DeviceID", DeviceID);
        mEditor.commit();
    }
    public void saveDeviceType(String DeviceType){
        mEditor.putString("DeviceType", DeviceType);
        mEditor.commit();
    }
    /**
     * 站点ID
     * @param StationID
     */
    public void saveStationID(String StationID){
        mEditor.putString("StationID", StationID);
        mEditor.commit();
    }
    public void saveOperationType(String OperationType){
        mEditor.putString("OperationType", OperationType);
        mEditor.commit();
    }
    /**
     *站点名称
     * @param StationName
     */
    public void saveStationName(String StationName){
        mEditor.putString("StationName", StationName);
        mEditor.commit();
    }

    /**
     *操作人ID
     * @param OperationID
     */
    public void saveOperationID(String OperationID){
        mEditor.putString("OperationID", OperationID);
        mEditor.commit();
    }
    /**
     *操作人姓名
     * @param OperationName
     */
    public void saveOperationName(String OperationName){
        mEditor.putString("OperationName", OperationName);
        mEditor.commit();
    }
    /**
     *蓝牙MAC地址
     * @param BlueToothAdd
     */
    public void saveBlueToothAdd(String BlueToothAdd){
        mEditor.putString("BlueToothAdd", BlueToothAdd);
        mEditor.commit();
    }
    public void saveStuffTagID(String StuffTagID){
        mEditor.putString("StaffTagID", StuffTagID);
        mEditor.commit();
    }
    /**
     * 整改人员
     * @param RectifyMan
     */
    public void saveRectifyMan(String RectifyMan){
        mEditor.putString("RectifyMan", RectifyMan);
        mEditor.commit();
    }
    /**
     * 登录方式
     * @param
     */
    public void saveLoginMode(String LoginMode){
        mEditor.putString("LoginMode", LoginMode);
        mEditor.commit();
    }
    /**
     * 登录方式
     * @param
     */
    public void saveLoginStuffMode(String LoginMode){
        mEditor.putString("LoginStaffMode", LoginMode);
        mEditor.commit();
    }
    public void saveCompanyCode(String CompanyCode){
        mEditor.putString("CompanyCode", CompanyCode);
        mEditor.commit();
    }
    public void saveCompanyName(String CompanyName){
        mEditor.putString("CompanyName", CompanyName);
        mEditor.commit();
    }
    public void saveCompanyPhone(String CompanyPhone){
        mEditor.putString("CompanyPhone", CompanyPhone);
        mEditor.commit();
    }
    /**
     *登录是否记住密码勾选框
     * @param IsChecked
     */
    public void saveIsChecked(boolean IsChecked){
        mEditor.putBoolean("IsChecked", IsChecked);
        mEditor.commit();
    }
    /**
     * 登录账户
     * @param Account
     */
    public void saveAccount(String Account){
        mEditor.putString("Account", Account);
        mEditor.commit();
    }

    /**
     * 登录密码
     * @param PassWord
     */
    public void savePass(String PassWord){
        mEditor.putString("PassWord", PassWord);
        mEditor.commit();
    }

    /**
     * 手持机数据库版本
     * @param DBVersion
     */
    public void saveDBVersion(String DBVersion){
        mEditor.putString("DBVersion", DBVersion);
        mEditor.commit();
    }


}
