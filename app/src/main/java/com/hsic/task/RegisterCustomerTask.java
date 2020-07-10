package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplModifyPassWord;
import com.hsic.listener.ImplRegisterCustomer;
import com.hsic.web.WebServiceHelper;

/**
 * Created by Administrator on 2020/6/29.
 */

public class RegisterCustomerTask extends AsyncTask<String,Void,HsicMessage> {
    ImplRegisterCustomer l;
    private Context context;
    private ProgressDialog dialog;
    WebServiceHelper webHelper;
    private String deviceID;
    public RegisterCustomerTask(Context context, ImplRegisterCustomer l) {
        this.context = context;
        this.l = l;
        dialog = new ProgressDialog(context);
        GetBasicInfo getBasicInfo=new GetBasicInfo(context);
        deviceID=getBasicInfo.getDeviceID();
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("提交用户信息中.....");
        dialog.setCancelable(false);
        dialog.show();
    }
    @Override
    protected HsicMessage doInBackground(String... strings) {
        HsicMessage hsicMess = new HsicMessage();
        webHelper = new WebServiceHelper(context);
        hsicMess = webHelper.RegisterCustomer(deviceID,strings[0]);
        return hsicMess;
    }




    @Override
    protected void onPostExecute(HsicMessage hsicMessage) {
        super.onPostExecute(hsicMessage);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.RegisterCustomerTaskEnd(hsicMessage);
    }

}
