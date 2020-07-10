package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplLoadStreetInfo;
import com.hsic.listener.ImplLogin;
import com.hsic.web.WebServiceHelper;

/**
 * Created by Administrator on 2020/6/24.
 */

public class LoadStreetInfoTask extends AsyncTask<String, Void, HsicMessage> {
    ImplLoadStreetInfo l;
    GetBasicInfo getBasicInfo;
    private Context context;
    private ProgressDialog dialog;
    WebServiceHelper web;
    public LoadStreetInfoTask(Context context,ImplLoadStreetInfo l){
        this.context=context;
        this.l=l;
        getBasicInfo=new GetBasicInfo(context);
        dialog = new ProgressDialog(context);
    }
    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        dialog.setMessage("加载街道基本信息...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected HsicMessage doInBackground(String... arg0) {
        // TODO Auto-generated method stub
        dialog.dismiss();
        web=new WebServiceHelper(context);
        HsicMessage hsicMess=new HsicMessage();
        hsicMess=web.loadStreetInfo(getBasicInfo.getDeviceID());
        return hsicMess;
    }

    @Override
    protected void onPostExecute(HsicMessage result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.LoadStreetInfoTaskEnd(result);
    }
}
