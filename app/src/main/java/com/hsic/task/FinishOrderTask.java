package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;


import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplFinishOrderTask;
import com.hsic.web.WebServiceHelper;

/**
 * Created by Administrator on 2018/8/14.
 */

public class FinishOrderTask extends AsyncTask<Void,Void,HsicMessage> {
    ImplFinishOrderTask l;
    private Context context;
    private ProgressDialog dialog;
    WebServiceHelper webHelper;
    private String requestStr,deviceIDStr,stationID;;
    public FinishOrderTask(Context context, ImplFinishOrderTask l, String deviceIDStr, String requestStr){
        this.context=context;
        this.l=l;
        dialog=new ProgressDialog(context);
        this.deviceIDStr=deviceIDStr;
        this.requestStr=requestStr;
        GetBasicInfo s=new GetBasicInfo(context);
        stationID=s.getStationID();

    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dialog.setMessage("正在提交订单信息");
        dialog.setCancelable(false);
        dialog.show();
    }
    @Override
    protected HsicMessage doInBackground(Void... voids) {
        HsicMessage hsicMess=new HsicMessage();
        webHelper=new WebServiceHelper(context);
        hsicMess= webHelper.finishOrder(requestStr, deviceIDStr,stationID);
        return hsicMess;
    }
    @Override
    protected void onPostExecute(HsicMessage hsicMessage) {
        super.onPostExecute(hsicMessage);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.GetImplFinishOrderTaskEnd(hsicMessage);
    }
}
