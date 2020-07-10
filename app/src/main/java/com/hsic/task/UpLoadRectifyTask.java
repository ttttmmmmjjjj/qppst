package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hsic.picture.UpLoadPIC;
import com.hsic.bean.FileRelationInfo;
import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.db.RectifyDB;
import com.hsic.listener.ImplUploadRectify;

import java.util.List;

/**
 * Created by Administrator on 2019/3/22.
 */

public class UpLoadRectifyTask extends AsyncTask<String, Void, HsicMessage> {
    RectifyDB rectifyDB;
    ImplUploadRectify l;
    GetBasicInfo getBasicInfo;
    private Context context;
    private ProgressDialog dialog;
    public UpLoadRectifyTask(Context context, ImplUploadRectify l){
        this.context=context;
        this.l=l;
        getBasicInfo=new GetBasicInfo(context);
        dialog = new ProgressDialog(context);
        rectifyDB=new RectifyDB(context);
    }
    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        dialog.setMessage("上传整改信息...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected HsicMessage doInBackground(String... arg0) {
        // TODO Auto-generated method stub
        dialog.dismiss();
        HsicMessage hsicMess=new HsicMessage();
        hsicMess=rectifyDB.upLoadRectifyInfo(context,getBasicInfo.getDeviceID(),getBasicInfo.getOperationID(),getBasicInfo.getStationID(),arg0[0]);
        if(hsicMess.getRespCode()==0){
            List<FileRelationInfo> list=rectifyDB.GetRectifyFileRelationInfo(getBasicInfo.getOperationID(),arg0[0]);
            rectifyDB.UpLoadA(list,getBasicInfo.getDeviceID(),context);
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
        l.UploadRectifyTaskEnd(result);
    }
}
