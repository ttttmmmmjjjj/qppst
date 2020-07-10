package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplExchangeGoods;
import com.hsic.web.WebServiceHelper;

/**
 * Created by Administrator on 2018/8/28.
 */

public class ExchangeGoodsTask extends AsyncTask<Void,Void,HsicMessage> {
    ImplExchangeGoods l;
    private Context context;
    private ProgressDialog dialog;
    WebServiceHelper webHelper;
    private String saleID,stationID,lastFullID,currentFullID,deviceIDStr;
    public ExchangeGoodsTask(Context context,ImplExchangeGoods l,String saleID,
                             String lastFullID,String currentFullID){
        this.context=context;
        this.l=l;
        this.saleID=saleID;
        this.lastFullID=lastFullID;
        this.currentFullID=currentFullID;
        GetBasicInfo s=new GetBasicInfo(context);
        stationID=s.getStationID();
        deviceIDStr=s.getDeviceID();
        dialog=new ProgressDialog(context);
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("正在提交换货信息");
        dialog.setCancelable(false);
        dialog.show();
    }
    @Override
    protected HsicMessage doInBackground(Void... voids) {
        HsicMessage hsicMess=new HsicMessage();
        webHelper=new WebServiceHelper(context);
        hsicMess= webHelper.exchangeGoods(deviceIDStr,stationID,saleID,lastFullID,currentFullID);
//        Log.e("调用结果", JSONUtils.toJsonWithGson(hsicMess));
        return hsicMess;
    }
    @Override
    protected void onPostExecute(HsicMessage hsicMessage) {
        super.onPostExecute(hsicMessage);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.ExchangeGoodsTaskEnd(hsicMessage);
    }
}
