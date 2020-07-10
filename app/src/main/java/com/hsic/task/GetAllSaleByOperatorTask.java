package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplGetAllSaleByOperatorTask;
import com.hsic.web.WebServiceHelper;

/**
 * Created by Administrator on 2018/8/15.
 */

public class GetAllSaleByOperatorTask extends AsyncTask<Void,Void,HsicMessage> {
    ImplGetAllSaleByOperatorTask l;
    private Context context;
    private ProgressDialog dialog;
    WebServiceHelper webHelper;
    private String requestStr, deviceIDStr;
    private String DeviceID, stationID, operationID, saleStatus;
    public GetAllSaleByOperatorTask(Context context, ImplGetAllSaleByOperatorTask l, String DeviceID,  String operationID, String saleStatus) {
        this.context = context;
        this.l = l;
        dialog = new ProgressDialog(context);
        this.DeviceID=DeviceID;
        this.operationID=operationID;
        GetBasicInfo s=new GetBasicInfo(context);
        stationID=s.getStationID();
        this.saleStatus=saleStatus;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("正在下载信息");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected HsicMessage doInBackground(Void... voids) {
        HsicMessage hsicMess = new HsicMessage();
        webHelper = new WebServiceHelper(context);
        hsicMess = webHelper.getAllSale(DeviceID, stationID, operationID, saleStatus);
//        Log.e("调用结果", JSONUtils.toJsonWithGson(hsicMess));
        return hsicMess;
    }

    @Override
    protected void onPostExecute(HsicMessage hsicMessage) {
        super.onPostExecute(hsicMessage);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.GetAllSaleByOperatorTaskEnd(hsicMessage);
    }
}
