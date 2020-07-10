package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hsic.bean.HsicMessage;
import com.hsic.listener.ImplModifyPassWord;
import com.hsic.web.WebServiceHelper;

/**
 * Created by Administrator on 2019/5/7.
 */

public class ModifyPassWordTask extends AsyncTask<String,Void,HsicMessage> {
    ImplModifyPassWord l;
    private Context context;
    private ProgressDialog dialog;
    WebServiceHelper webHelper;
    public ModifyPassWordTask(Context context, ImplModifyPassWord l) {
        this.context = context;
        this.l = l;
        dialog = new ProgressDialog(context);
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("正在修改密码");
        dialog.setCancelable(false);
        dialog.show();
    }
    @Override
    protected HsicMessage doInBackground(String... strings) {
        HsicMessage hsicMess = new HsicMessage();
        webHelper = new WebServiceHelper(context);
        hsicMess = webHelper.UpdateEmployeeMM(strings[0],strings[1]);
        return hsicMess;
    }




    @Override
    protected void onPostExecute(HsicMessage hsicMessage) {
        super.onPostExecute(hsicMessage);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.ModifyPassWordTaskEnd(hsicMessage);
    }
}
