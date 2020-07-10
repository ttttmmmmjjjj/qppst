package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplSearchEmployee;
import com.hsic.web.WebServiceHelper;

/**
 * Created by Administrator on 2019/1/14.
 */

public class SearchEmployeeTask extends AsyncTask<String, Void, HsicMessage> {
    ImplSearchEmployee l;
    private Context context;
    private ProgressDialog dialog;
    WebServiceHelper web;
    GetBasicInfo getBasicInfo;
    public SearchEmployeeTask(Context context, ImplSearchEmployee l){
        this.context=context;
        this.l=l;
        getBasicInfo=new GetBasicInfo(context);
        dialog = new ProgressDialog(context);
    }
    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        dialog.setMessage("正在加载员工信息...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected HsicMessage doInBackground(String... arg0) {
        // TODO Auto-generated method stub
        dialog.dismiss();
        web=new WebServiceHelper(context);
        HsicMessage hsicMess=new HsicMessage();
        hsicMess=web.SearchEmployee(getBasicInfo.getDeviceID(),arg0[0]);
        return hsicMess;
    }

    @Override
    protected void onPostExecute(HsicMessage result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.SearchEmployeeTaskEnd(result);
    }
}
