package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplAddNewSale;
import com.hsic.listener.ImplCancelOrderTask;
import com.hsic.web.WebServiceHelper;

/**
 * Created by Administrator on 2020/6/18.
 */

public class AddNewSaleTask  extends AsyncTask<Void,Void,HsicMessage> {
    ImplAddNewSale l;
    private Context context;
    private ProgressDialog dialog;
    WebServiceHelper webHelper;
    private String deviceIDStr,RequestData;;
    public AddNewSaleTask(Context context, ImplAddNewSale l, String RequestData) {
        this.context = context;
        this.l = l;
        dialog = new ProgressDialog(context);
        GetBasicInfo s=new GetBasicInfo(context);
        deviceIDStr=s.getDeviceID();
        this.RequestData=RequestData;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("订单新增。。。。");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected HsicMessage doInBackground(Void... voids) {
        HsicMessage hsicMess = new HsicMessage();
        webHelper = new WebServiceHelper(context);
        hsicMess = webHelper.AddSale_fendan(deviceIDStr,RequestData);
        return hsicMess;
    }

    @Override
    protected void onPostExecute(HsicMessage hsicMessage) {
        super.onPostExecute(hsicMessage);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.AddNewSaleTaskEnd(hsicMessage);
    }
}
