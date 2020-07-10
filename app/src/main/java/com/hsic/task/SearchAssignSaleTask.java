package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplSearchAssignSale;
import com.hsic.web.WebServiceHelper;

/**
 * Created by Administrator on 2019/2/26.
 */

public class SearchAssignSaleTask extends AsyncTask<String, Void, HsicMessage> {
    GetBasicInfo getBasicInfo;
    private Context context;
    private ProgressDialog dialog;
    WebServiceHelper web;
    ImplSearchAssignSale l;
    long start;
    public SearchAssignSaleTask(Context context,ImplSearchAssignSale l){
        this.context=context;
        this.l=l;
        getBasicInfo=new GetBasicInfo(context);
        dialog = new ProgressDialog(context);
        start = System.currentTimeMillis();

    }
    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        dialog.setMessage("下载订单中...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected HsicMessage doInBackground(String... arg0) {
        // TODO Auto-generated method stub
        dialog.dismiss();
        web=new WebServiceHelper(context);
        HsicMessage hsicMess=new HsicMessage();
        hsicMess=web.SearchAssignSale(getBasicInfo.getDeviceID(),arg0[0]);
        return hsicMess;
    }

    @Override
    protected void onPostExecute(HsicMessage result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        long end = System.currentTimeMillis();
        Log.e("下载SearchAssignSale","="+(end -start));
        dialog.setCancelable(true);
        dialog.dismiss();
        l.SearchAssignSaleTaskEnd(result);
    }
}
