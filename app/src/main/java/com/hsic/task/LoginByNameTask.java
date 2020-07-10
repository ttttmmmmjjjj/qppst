package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplLoginByName;
import com.hsic.web.WebServiceHelper;

/**
 * Created by Administrator on 2019/4/11.
 */

public class LoginByNameTask extends AsyncTask<String,Void,HsicMessage> {
    ImplLoginByName l;
    private Context context;
    WebServiceHelper webHelper;
    private String DeviceID,StationID;
    private ProgressDialog dialog;
    public LoginByNameTask(Context context,ImplLoginByName l){
        this.l=l;
        this.context=context;
        dialog=new ProgressDialog(context);
        GetBasicInfo s=new GetBasicInfo(context);
        DeviceID=s.getDeviceID();
        StationID=s.getStationID();
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("正在获取信息");
        dialog.setCancelable(false);
        dialog.show();
    }
    @Override
    protected HsicMessage doInBackground(String... arg0) {
        HsicMessage hsicMess=new HsicMessage();
        webHelper=new WebServiceHelper(context);
        hsicMess= webHelper.CustomerLogin_CustomerName(DeviceID,arg0[0],arg0[1]);
        return hsicMess;
    }
    @Override
    protected void onPostExecute(HsicMessage hsicMessage) {
        super.onPostExecute(hsicMessage);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.LoginByNameTaskEnd(hsicMessage);
    }
}
