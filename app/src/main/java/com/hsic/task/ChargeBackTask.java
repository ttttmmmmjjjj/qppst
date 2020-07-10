package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplChargeBack;
import com.hsic.web.WebServiceHelper;

/**
 * Created by Administrator on 2019/4/25.
 */

public class ChargeBackTask extends AsyncTask<String,Void,HsicMessage> {
    ImplChargeBack l;
    GetBasicInfo getBasicInfo;
    private Context context;
    private ProgressDialog dialog;
    WebServiceHelper webHelper;
    private String deviceIDStr;
    public ChargeBackTask(Context context,ImplChargeBack l){
        this.context=context;
        this.l=l;
        getBasicInfo=new GetBasicInfo(context);
        dialog = new ProgressDialog(context);
    }


    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        dialog.setMessage("正在退单...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected HsicMessage doInBackground(String... arg0) {
        // TODO Auto-generated method stub
        dialog.dismiss();
        webHelper=new WebServiceHelper(context);
        HsicMessage hsicMess=new HsicMessage();
        hsicMess=webHelper.Chargeback(getBasicInfo.getDeviceID(),arg0[0]);
        return hsicMess;
    }

    @Override
    protected void onPostExecute(HsicMessage result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.ChargeBackTaskEnd(result);
    }
}
