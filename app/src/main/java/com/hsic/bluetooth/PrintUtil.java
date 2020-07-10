package com.hsic.bluetooth;
import android.content.Context;
import android.content.SharedPreferences;


/**
 *
 */
public class PrintUtil {
    private static final String FILENAME = "DeviceSetting";
    private static final String DEFAULT_BLUETOOTH_DEVICE_ADDRESS = "BlueToothAdd";//蓝牙设备地址
    public static void setDefaultBluetoothDeviceAddress(Context mContext, String value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DEFAULT_BLUETOOTH_DEVICE_ADDRESS, value);
        editor.apply();
        AppInfo.btAddress = value;
    }

    public static String getDefaultBluethoothDeviceAddress(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(DEFAULT_BLUETOOTH_DEVICE_ADDRESS, "");
    }
}
