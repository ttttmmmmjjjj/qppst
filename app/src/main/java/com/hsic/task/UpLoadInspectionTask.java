package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import com.hsic.picture.UpLoadPIC;
import com.hsic.bean.FileRelationInfo;
import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.db.DeliveryDB;
import com.hsic.listener.ImplUpLoadInspection;

import java.util.List;

/**
 * Created by Administrator on 2019/3/18.
 */

public class UpLoadInspectionTask extends AsyncTask<String, Void, HsicMessage> {
    GetBasicInfo getBasicInfo;
    private Context context;
    private ProgressDialog dialog;
    ImplUpLoadInspection l;
    DeliveryDB deliveryDB;
    private int ret=0;
    public UpLoadInspectionTask(Context context, ImplUpLoadInspection l){
        this.context=context;
        this.l=l;
        getBasicInfo=new GetBasicInfo(context);
        dialog = new ProgressDialog(context);
        deliveryDB=new DeliveryDB(context);
    }
    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        dialog.setMessage("上传安检信息...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected HsicMessage doInBackground(String... arg0) {
        // TODO Auto-generated method stub
        dialog.dismiss();
        HsicMessage hsicMess=new HsicMessage();
        hsicMess=deliveryDB.uploadXJInfo(context,getBasicInfo.getDeviceID(),getBasicInfo.getOperationID(),getBasicInfo.getStationID(),arg0[0],arg0[1]);
        if(hsicMess.getRespCode()==0||hsicMess.getRespCode()==2){
            List<FileRelationInfo> list=deliveryDB.GetXJFileRelationInfo(getBasicInfo.getOperationID(),arg0[0]);
            deliveryDB.UpLoadA(list,getBasicInfo.getDeviceID(),context);
        }
        UpLoadPIC PIC=new UpLoadPIC();
        PIC.upPicture(context, getBasicInfo.getDeviceID());
        return hsicMess;
    }

    @Override
    protected void onPostExecute(HsicMessage result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.UpLoadInspectionTaskEnd(result);
    }

}
