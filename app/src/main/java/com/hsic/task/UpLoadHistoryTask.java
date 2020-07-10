package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hsic.picture.UpLoadPIC;
import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.db.AJDB;
import com.hsic.db.DeliveryDB;
import com.hsic.db.RectifyDB;
import com.hsic.listener.ImplUpHistory;

/**
 * Created by Administrator on 2019/3/20.
 */

public class UpLoadHistoryTask extends AsyncTask<String, Void, HsicMessage> {
    DeliveryDB deliveryDB;
    GetBasicInfo getBasicInfo;
    private Context context;
    private ProgressDialog dialog;
    AJDB ajdb;
    RectifyDB rectifyDB;
    ImplUpHistory l;
    public UpLoadHistoryTask(Context context,ImplUpHistory l){
        this.context=context;
        getBasicInfo=new GetBasicInfo(context);
        dialog = new ProgressDialog(context);
        deliveryDB=new DeliveryDB(context);
        ajdb=new AJDB(context);
        rectifyDB=new RectifyDB(context);
        this.l=l;
    }
    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        dialog.setMessage("上传信息...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected HsicMessage doInBackground(String... arg0) {
        // TODO Auto-generated method stub
        dialog.dismiss();
        HsicMessage hsicMess=new HsicMessage();
        //历史销售表
        deliveryDB.UpHistorySale(context,getBasicInfo.getDeviceID(),getBasicInfo.getOperationID(),getBasicInfo.getStationID());
        //历史安检表
        deliveryDB.UpHistoryXJ(context,getBasicInfo.getDeviceID(),getBasicInfo.getOperationID(),getBasicInfo.getStationID());
        //历史安检表
        ajdb.UpHistoryAJ(context,getBasicInfo.getDeviceID(),getBasicInfo.getOperationID(),getBasicInfo.getStationID());
        //历史整改表
        rectifyDB.UpHistoryRectify(context,getBasicInfo.getDeviceID(),getBasicInfo.getOperationID(),getBasicInfo.getStationID());
        UpLoadPIC PIC=new UpLoadPIC();
        hsicMess=PIC.upPicture(context, getBasicInfo.getDeviceID());
        return hsicMess;
    }

    @Override
    protected void onPostExecute(HsicMessage result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.UpLoadHistoryTaskEnd(result);
    }
}
