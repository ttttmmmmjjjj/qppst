package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplSearchSale;
import com.hsic.web.WebServiceHelper;

/**
 * 分单获取订单信息
 * Created by Administrator on 2019/1/7.
 */

public class SearchSaleTask extends AsyncTask<String, Void, HsicMessage> {
    ImplSearchSale l;
    private Context context;
    private ProgressDialog dialog;
    WebServiceHelper web;
    GetBasicInfo getBasicInfo;
    public SearchSaleTask(Context context, ImplSearchSale l){
        this.context=context;
        this.l=l;
        getBasicInfo=new GetBasicInfo(context);
        dialog = new ProgressDialog(context);
    }
    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        dialog.setMessage("正在加载订单信息...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected HsicMessage doInBackground(String... arg0) {
        // TODO Auto-generated method stub
        dialog.dismiss();
        web=new WebServiceHelper(context);
        HsicMessage hsicMess=new HsicMessage();
        hsicMess=web.SearchSale(getBasicInfo.getDeviceID(),arg0[0]);
        return hsicMess;
    }

    @Override
    protected void onPostExecute(HsicMessage result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.SearchSaleTaskEnd(result);
    }
}
