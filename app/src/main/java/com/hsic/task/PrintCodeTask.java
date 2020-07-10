package com.hsic.task;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;


import com.hsic.bean.UserInfoDoorSale;
import com.hsic.bll.GetBasicInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Administrator on 2018/8/16.
 */

public class PrintCodeTask extends AsyncTask<Void, Void, Void> {
    private byte[]  saleID;
    private Context context = null;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter
            .getDefaultAdapter();
    private boolean isConnection = false;
    private BluetoothDevice device = null;
    private static BluetoothSocket bluetoothSocket = null;
    private static OutputStream outputStream;
    private static final UUID uuid = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    SharedPreferences deviceSetting;
    String bluetoothadd = "";// 蓝牙MAC
    UserInfoDoorSale userInfoDoorSale;
    private ProgressDialog dialog;
    GetBasicInfo basicInfo;

    public PrintCodeTask(Context context, byte[]  saleID,UserInfoDoorSale userInfoDoorSale){
        this.context=context;
        this.saleID=saleID;
        deviceSetting = context.getSharedPreferences("DeviceSetting", 0);
        bluetoothadd = deviceSetting.getString("BlueToothAdd", "");// 蓝牙MAC
        this.userInfoDoorSale=userInfoDoorSale;
        dialog = new ProgressDialog(context);
        basicInfo=new GetBasicInfo(context);

    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("正在打印信息");
        dialog.setCancelable(false);
        dialog.show();
    }
    @Override
    protected Void doInBackground(Void... voids) {
//        try {
            int pCount = 0;
            pCount = Integer.parseInt("1");
            //测试(最新测试)
            String Intret = connectBT();
//            PrintUtils.setOutputStream(outputStream);
//            PrintUtils.selectCommand(PrintUtils.RESET);
//            PrintUtils.selectCommand(PrintUtils.LINE_SPACING_DEFAULT);
////            PrintUtils.selectCommand(PrintUtils.DOUBLE_HEIGHT_WIDTH);
//            PrintUtils.selectCommand(PrintUtils.ALIGN_CENTER);
////            PrintUtils.printText("订单信息\n");
//            PrintUtils.selectCommand(PrintUtils.ALIGN_CENTER);
            printCode(saleID, false);
//            PrintUtils.selectCommand(PrintUtils.RESET);
////            PrintUtils.printText("\n");
//            PrintUtils.selectCommand(PrintUtils.NORMAL);
//            PrintUtils.selectCommand(PrintUtils.ALIGN_CENTER);
//            PrintUtils.printText(userInfoDoorSale.getSaleID()+"\n\n");
//            PrintUtils.selectCommand(PrintUtils.NORMAL);
//            PrintUtils.selectCommand(PrintUtils.ALIGN_LEFT);
//            PrintUtils.printText(PrintUtils.printTwoData("用户站点", userInfoDoorSale.getStationName()+"\n"));
//            PrintUtils.printText(PrintUtils.printTwoData("订单编号", userInfoDoorSale.getSaleID()+"\n"));
//            PrintUtils.printText(PrintUtils.printTwoData("用户编号", userInfoDoorSale.getUserID()+"\n"));
//            PrintUtils.printText(PrintUtils.printTwoData("用户类型", userInfoDoorSale.getUserType()+"\n"));
//            PrintUtils.printText(PrintUtils.printTwoData("用户姓名", userInfoDoorSale.getUserName()+"\n"));
//            PrintUtils.printText(PrintUtils.printTwoData("电话", userInfoDoorSale.getPhoneNumber()+"\n"));
//            PrintUtils.printText(PrintUtils.printTwoData("", userInfoDoorSale.getAddress()+"\n"));
//            PrintUtils.printText("--------------------------------\n");
//            PrintUtils.selectCommand(PrintUtils.BOLD);
//            PrintUtils.printText(PrintUtils.printThreeData("项目", "数量", "金额\n"));
//            List<UserInfoDoorSale.GoodsInfo> goodsInfo_List2=new ArrayList<UserInfoDoorSale.GoodsInfo>();
//            goodsInfo_List2=userInfoDoorSale.getGoodsInfo();
//            int size =goodsInfo_List2.size();
//            for(int i=0;i<size;i++){
//                PrintUtils.printText(PrintUtils.printThreeData(goodsInfo_List2.get(i).getGoodsName(), goodsInfo_List2.get(i).getGoodsCount(),goodsInfo_List2.get(i).getUnitPrice() +"\n"));
//            }
//            PrintUtils.selectCommand(PrintUtils.BOLD_CANCEL);
//            PrintUtils.printText("--------------------------------\n");
//            PrintUtils.printText(PrintUtils.printTwoData("空瓶:", userInfoDoorSale.getEmptyNO()+"\n"));
//            PrintUtils.printText(PrintUtils.printTwoData("满瓶:", userInfoDoorSale.getFullNO()+"\n"));
//            PrintUtils.printText("--------------------------------\n");
//            PrintUtils.printText(PrintUtils.printTwoData("合计", userInfoDoorSale.getTotalPrice()+"\n"));
//            PrintUtils.printText(PrintUtils.printTwoData("操作人", basicInfo.getOperationName()+"\n"));
//            PrintUtils.printText(PrintUtils.printTwoData("完成时间", userInfoDoorSale.getOperationTime()+"\n"));
//            PrintUtils.printText(PrintUtils.printTwoData("操作站点", basicInfo.getStationName()+"\n"));
//            PrintUtils.printText("\n\n\n\n\n");
//        } catch (Exception ex) {
//            Log.e
//            ex.toString();
//        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dialog.setCancelable(true);
        dialog.dismiss();
        finish();
    }

    public void printCode(byte[]  saleID, boolean charactor) {
        try {
            outputStream.write(saleID, 0, saleID.length);
            outputStream.flush();
        } catch (Exception ex) {
            Log.e("ppp",ex.toString());
        }
    }

    public String connectBT() {
        String log = "connectBT()";
        // 先检查该设备是否支持蓝牙
        if (bluetoothAdapter == null) {
            return "1";// 该设备没有蓝牙功能
        } else {
            // 检查蓝牙是否打开
            boolean b = bluetoothAdapter.isEnabled();
            if (!bluetoothAdapter.isEnabled()) {
                // 若没打开，先打开蓝牙
                bluetoothAdapter.enable();
                System.out.print("蓝牙未打开");
                return "2";// 蓝牙未打开，程序强制打开蓝牙
            } else {
                try {
                    this.device = bluetoothAdapter
                            .getRemoteDevice(bluetoothadd);
                    if (!this.isConnection) {
                        bluetoothSocket = this.device
                                .createRfcommSocketToServiceRecord(uuid);
                        bluetoothSocket.connect();
                        outputStream = bluetoothSocket.getOutputStream();
                        this.isConnection = true;
                    }
                } catch (Exception ex) {
                    System.out.print("远程获取设备出现异常" + ex.toString());
                    return "3";// 获取设备出现异常
                }
            }
            return "0";// 连接成功
        }

    }

    private void finish() {
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            if (outputStream != null) {
                outputStream.close();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
