package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import com.hsic.picture.UpLoadPIC;
import com.hsic.bean.FileRelationInfo;
import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.db.DKSaleDB;
import com.hsic.db.DeliveryDB;
import com.hsic.listener.ImplUpDKInspectionTask;
import com.hsic.web.WebServiceHelper;

import java.util.List;

/**
 * Created by Administrator on 2019/7/4.
 */

public class UpDKInspectionTask extends AsyncTask<String, Void, HsicMessage> {
    ImplUpDKInspectionTask l;
    private Context context;
    private ProgressDialog dialog;
    WebServiceHelper webHelper;
    GetBasicInfo getBasicInfo;
    DeliveryDB deliveryDB;
    DKSaleDB dkSaleDB;
    public UpDKInspectionTask(Context context, ImplUpDKInspectionTask l) {
        this.context = context;
        this.l = l;
        dialog = new ProgressDialog(context);
        deliveryDB=new DeliveryDB(context);
        dkSaleDB=new DKSaleDB(context);
        getBasicInfo=new GetBasicInfo(context);
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("正在上传信息");
        dialog.setCancelable(false);
        dialog.show();
    }
    @Override
    protected HsicMessage doInBackground(String... strings) {
        HsicMessage hsicMess = new HsicMessage();
        webHelper = new WebServiceHelper(context);
        hsicMess=dkSaleDB.uploadDKSaleInfo(context,getBasicInfo.getDeviceID(),getBasicInfo.getDeviceID(),
                getBasicInfo.getStationID(),strings[0],strings[1]);
        if(hsicMess.getRespCode()==0){
            List<FileRelationInfo> list=deliveryDB.GetXJFileRelationInfo(getBasicInfo.getOperationID(),strings[0]);
            deliveryDB.UpLoadA(list,getBasicInfo.getDeviceID(),context);
        }

        UpLoadPIC PIC=new UpLoadPIC();
        PIC.upPicture(context, getBasicInfo.getDeviceID());
        return hsicMess;
    }




    @Override
    protected void onPostExecute(HsicMessage hsicMessage) {
        super.onPostExecute(hsicMessage);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.UpDKInspectionEnd(hsicMessage);
    }
}
