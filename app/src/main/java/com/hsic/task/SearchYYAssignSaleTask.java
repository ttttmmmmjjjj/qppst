package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;


import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplSearchYYAssignSale;
import com.hsic.web.WebServiceHelper;
/**
 * Created by Administrator on 2019/10/25.
 */

public class SearchYYAssignSaleTask extends AsyncTask<Void,Void,HsicMessage> {
    ImplSearchYYAssignSale l;
    private Context context;
    private ProgressDialog dialog;
    WebServiceHelper webHelper;
    private String stationID,deviceIDStr;
    public SearchYYAssignSaleTask(Context context, ImplSearchYYAssignSale l) {
        this.context = context;
        this.l = l;
        dialog = new ProgressDialog(context);
        GetBasicInfo s=new GetBasicInfo(context);
        stationID=s.getStationID();
        this.deviceIDStr = s.getDeviceID();
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
        hsicMess = webHelper.SearchYYAssignSale(deviceIDStr,stationID);
        return hsicMess;
    }

    @Override
    protected void onPostExecute(HsicMessage hsicMessage) {
        super.onPostExecute(hsicMessage);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.SearchYYAssignSaleTaskEnd(hsicMessage);
    }
}
