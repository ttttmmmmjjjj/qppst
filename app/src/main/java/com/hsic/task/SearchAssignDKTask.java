package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hsic.bean.HsicMessage;
import com.hsic.listener.ImplSearchAssignDKTask;
import com.hsic.web.WebServiceHelper;

/**
 * Created by Administrator on 2019/7/4.
 */

public class SearchAssignDKTask extends AsyncTask<String, Void, HsicMessage> {
    ImplSearchAssignDKTask l;
    private Context context;
    private ProgressDialog dialog;
    WebServiceHelper webHelper;
    public SearchAssignDKTask(Context context, ImplSearchAssignDKTask l) {
        this.context = context;
        this.l = l;
        dialog = new ProgressDialog(context);
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("正在加载信息");
        dialog.setCancelable(false);
        dialog.show();
    }
    @Override
    protected HsicMessage doInBackground(String... strings) {
        HsicMessage hsicMess = new HsicMessage();
        webHelper = new WebServiceHelper(context);
        hsicMess = webHelper.SearchAssignDKSaleInfo(strings[0],strings[1]);
        return hsicMess;
    }




    @Override
    protected void onPostExecute(HsicMessage hsicMessage) {
        super.onPostExecute(hsicMessage);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.SearchAssignDKTaskEnd(hsicMessage);
    }
}
