package com.hsic.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.hsic.bean.HsicMessage;
import com.hsic.bean.SaleAll;
import com.hsic.bll.GetBasicInfo;
import com.hsic.db.AJDB;
import com.hsic.db.DeliveryDB;
import com.hsic.db.RectifyDB;
import com.hsic.notification.NewSaleNotificationReceiver;
import com.hsic.notification.SaleStateChangeNotificationReceiver;
import com.hsic.picture.UpLoadPIC;
import com.hsic.qpmanager.util.json.JSONUtils;
import com.hsic.web.WebServiceHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/4/2.
 */

public class PushSaleService extends Service {
    private boolean isRunning = false;
    private boolean flag = true;
    Intent i;
    int h;
    private String stuffid = "";
    GetBasicInfo getBasicInfo;
    DeliveryDB deliveryDB;
    String EmployeeID,StationID;
    PowerManager.WakeLock wakeLock = null;
    AJDB ajdb;
    RectifyDB rectifyDB;
    @Override
    public void onCreate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
        String time = df.format(new Date());// new Date()为获取当前系统时间
        String hour=time.substring(11, 13);
        h = Integer.parseInt(hour);
        getBasicInfo=new GetBasicInfo(this);
        deliveryDB=new DeliveryDB(this);
        EmployeeID=getBasicInfo.getOperationID();
        StationID=getBasicInfo.getStationID();
        ajdb=new AJDB(this);
        rectifyDB=new RectifyDB(this);
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        GetSaleThread.interrupted();
        return super.onUnbind(intent);
    }
    @Override
    public void onStart(Intent intent, int startId) {
        i=intent;
        if (!isRunning) {
            new GetSaleThread().start();
        }
        acquireWakeLock();
    }

    class GetSaleThread extends Thread {
        @Override
        public void run() {
            while (flag) {
                isRunning = true;
                boolean isNet=false;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
                String time = df.format(new Date());// new Date()为获取当前系统时间
                String hour=time.substring(11, 13);
                h = Integer.parseInt(hour);
                Log.e(" h  h  h  h ","hhhhh="+h);
                if ((h<=7 || h >= 17)) {
//                    flag = false;//线程停止标识
//                    stopService(i);
                    isNet=false;
                }else{
                    isNet=checkNetworkState();
                }
                try {
                    Thread.sleep(60 * 1000*5);
                    int counts = 0;
                    int zfCounts=0;
                    int urgeCounts=0;
                    HsicMessage mess = new HsicMessage();
                    stuffid = getBasicInfo.getOperationID();
                    Log.e(" isNet", JSONUtils.toJsonWithGson(isNet));
                    if(isNet){
//                        sendNotification2();//test
                        WebServiceHelper wsHelper = new WebServiceHelper(PushSaleService.this);
                        mess = wsHelper.SearchAssignSale(getBasicInfo.getDeviceID(),stuffid);
                        if (mess.getRespCode() == 0) {
                            List<SaleAll> list1=new ArrayList<SaleAll>();
                            list1= JSONUtils.toListWithGson(mess.getRespMsg(), new TypeToken<List<SaleAll>>() {
                            }.getType());//数据来源
                            int size=list1.size();
                            StringBuffer saleID_Web=new StringBuffer();
                            List<String> pushSale = new ArrayList<String>();
                            for(int i=0;i<size;i++){
                                String SaleID=list1.get(i).getSale().getSaleID();
                                String urgeStatus=list1.get(i).getSale().getUrgeGasInfoStatus();
                                saleID_Web.append(SaleID+",");
                                if(!deliveryDB.isExist(EmployeeID,StationID,SaleID)){
                                    pushSale.add(SaleID);
                                }else{
                                    //查看催单状态
                                    if(urgeStatus.equals("0")){
                                        urgeCounts++;
                                    }
                                }
                            }
                            String saleID=saleID_Web.toString();
                            int l=saleID.length();
                            saleID=saleID.substring(0,l);
                            //本地订单和后台对比
                            List<Map<String, String>> data = new ArrayList<Map<String, String>>();
                            data=deliveryDB.saleID(EmployeeID,StationID);
                            for (int h = 0; h < data.size(); h++) {
                                String tmp=data.get(h).get("SaleID");
                                if (!saleID.contains(tmp)) {
                                    //发送通知，并更新
                                    deliveryDB.updateSaleStatus(EmployeeID,StationID,tmp);
                                    zfCounts++;
                                }
                            }
                            counts=pushSale.size();
                        }
                        if(counts>0){
                            sendNotification();
                        }
                        if(zfCounts>0){
                            sendNotification2();
                            counts=counts+zfCounts;
                        }
                        counts=counts+urgeCounts;
                        //发送广播
                        Intent intent = new Intent();
                        intent.setAction("com.hsic.tmj.qppst.activity.NewsReceiver");
                        intent.putExtra("NewCount", counts);
                        PushSaleService.this.sendBroadcast(intent);
                        /**
                         *
                         */
                        //历史销售表
                        deliveryDB.UpHistorySale(PushSaleService.this,getBasicInfo.getDeviceID(),getBasicInfo.getOperationID(),getBasicInfo.getStationID());
                        //历史安检表
                        deliveryDB.UpHistoryXJ(PushSaleService.this,getBasicInfo.getDeviceID(),getBasicInfo.getOperationID(),getBasicInfo.getStationID());
                        //历史安检表
                        ajdb.UpHistoryAJ(PushSaleService.this,getBasicInfo.getDeviceID(),getBasicInfo.getOperationID(),getBasicInfo.getStationID());
                        //历史整改表
                        rectifyDB.UpHistoryRectify(PushSaleService.this,getBasicInfo.getDeviceID(),getBasicInfo.getOperationID(),getBasicInfo.getStationID());
                        UpLoadPIC PIC=new UpLoadPIC();
                        PIC.upPicture(PushSaleService.this, getBasicInfo.getDeviceID());

                    }

                }catch(Exception ex){
                    ex.printStackTrace();
                    flag = false;
                }
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        GetSaleThread.interrupted();
        flag = false;
        releaseWakeLock();
    }
    /**
     * 20180328
     * 增加实时发送广播：消息栏提醒
     */
    private void sendNotification() {
        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NewSaleNotificationReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), pendingIntent);
    }
    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    private void acquireWakeLock(){
        if (null == wakeLock) {
            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, "PushSaleService");
            if (null != wakeLock) {
                wakeLock.acquire();
            }
        }
    }
    //释放设备电源锁
    private void releaseWakeLock(){
        if (null != wakeLock) {
            wakeLock.release();
            wakeLock = null;
        }
    }
    /**
     * 检测网络是否连接
     *
     * @return
     */
    private boolean checkNetworkState() {
        boolean flag = false;
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        return flag;
    }
    private void sendNotification2() {
        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, SaleStateChangeNotificationReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), pendingIntent);
    }

}
