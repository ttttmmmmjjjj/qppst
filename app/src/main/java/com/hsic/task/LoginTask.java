package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplLogin;
import com.hsic.web.WebServiceHelper;

/**
 * Created by Administrator on 2019/2/25.
 */

public class LoginTask extends AsyncTask<String, Void, HsicMessage> {
    GetBasicInfo getBasicInfo;
    private Context context;
    private ProgressDialog dialog;
    WebServiceHelper web;
    ImplLogin l;
    public LoginTask(Context context,ImplLogin l){
        this.context=context;
        this.l=l;
        getBasicInfo=new GetBasicInfo(context);
        dialog = new ProgressDialog(context);
    }
    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        dialog.setMessage("正在登录...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected HsicMessage doInBackground(String... arg0) {
        // TODO Auto-generated method stub
        dialog.dismiss();
        web=new WebServiceHelper(context);
        HsicMessage hsicMess=new HsicMessage();
        hsicMess=web.EmployeeLogin(getBasicInfo.getDeviceID(),arg0[0],arg0[1]);
        return hsicMess;
    }

    @Override
    protected void onPostExecute(HsicMessage result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.LoginTaskEnd(result);
    }
}
