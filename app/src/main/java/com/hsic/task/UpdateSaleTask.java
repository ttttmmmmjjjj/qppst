package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplUpdateSale;
import com.hsic.web.WebServiceHelper;

/**
 * 分单时:更新分单数据
 * Created by Administrator on 2019/1/8.
 */

public class UpdateSaleTask extends AsyncTask<String, Void, HsicMessage> {
    private Context context;
    private ProgressDialog dialog;
    WebServiceHelper web;
    GetBasicInfo getBasicInfo;
    ImplUpdateSale l;
    public UpdateSaleTask(Context context, ImplUpdateSale l){
        this.context=context;
        this.l=l;
        getBasicInfo=new GetBasicInfo(context);
        dialog = new ProgressDialog(context);
    }
    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        dialog.setMessage("上传销售信息...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected HsicMessage doInBackground(String... arg0) {
        // TODO Auto-generated method stub
        dialog.dismiss();
        web=new WebServiceHelper(context);
        HsicMessage hsicMess=new HsicMessage();
        hsicMess=web.UpdateSale(getBasicInfo.getDeviceID(),arg0[0]);
        return hsicMess;
    }

    @Override
    protected void onPostExecute(HsicMessage result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.UpdateSaleTaskEnd(result);
    }
}
