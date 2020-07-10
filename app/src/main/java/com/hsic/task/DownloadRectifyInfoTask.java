package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplDownloadRectifyInfo;
import com.hsic.web.WebServiceHelper;

/**
 * Created by Administrator on 2019/3/21.
 */

public class DownloadRectifyInfoTask extends AsyncTask<String, Void, HsicMessage> {
    ImplDownloadRectifyInfo l;
    GetBasicInfo getBasicInfo;
    private Context context;
    private ProgressDialog dialog;
    WebServiceHelper web;
    public DownloadRectifyInfoTask(Context context,ImplDownloadRectifyInfo l){
        this.context=context;
        this.l=l;
        getBasicInfo=new GetBasicInfo(context);
        dialog = new ProgressDialog(context);
    }
    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        dialog.setMessage("下载整改信息中...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected HsicMessage doInBackground(String... arg0) {
        // TODO Auto-generated method stub
        dialog.dismiss();
        web=new WebServiceHelper(context);
        HsicMessage hsicMess=new HsicMessage();
        hsicMess=web.DownloadRectifyInfo(getBasicInfo.getDeviceID(),getBasicInfo.getOperationID());
        return hsicMess;
    }

    @Override
    protected void onPostExecute(HsicMessage result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.DownloadRectifyInfoTaskEnd(result);
    }
}
