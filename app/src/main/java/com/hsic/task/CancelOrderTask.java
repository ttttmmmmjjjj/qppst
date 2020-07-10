package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplCancelOrderTask;
import com.hsic.web.WebServiceHelper;

/**
 * Created by Administrator on 2018/8/15.
 */

public class CancelOrderTask extends AsyncTask<Void,Void,HsicMessage> {
    ImplCancelOrderTask l;
    private Context context;
    private ProgressDialog dialog;
    WebServiceHelper webHelper;
    private String stationID, deviceIDStr,RequestData;;
    public CancelOrderTask(Context context, ImplCancelOrderTask l, String deviceIDStr, String RequestData) {
        this.context = context;
        this.l = l;
        dialog = new ProgressDialog(context);
        this.deviceIDStr = deviceIDStr;
        GetBasicInfo s=new GetBasicInfo(context);
        stationID=s.getStationID();
        this.RequestData=RequestData;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("正在作废订单");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected HsicMessage doInBackground(Void... voids) {
        HsicMessage hsicMess = new HsicMessage();
        webHelper = new WebServiceHelper(context);
        hsicMess = webHelper.cancelOrder(deviceIDStr,stationID,RequestData);
        return hsicMess;
    }

    @Override
    protected void onPostExecute(HsicMessage hsicMessage) {
        super.onPostExecute(hsicMessage);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.CancelOrderTaskEnd(hsicMessage);
    }
}
