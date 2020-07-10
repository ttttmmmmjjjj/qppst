package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplGetBill;
import com.hsic.web.WebServiceHelper;

/**
 * Created by Administrator on 2019/7/25.
 */

public class GetBillTask extends AsyncTask<String,Void,HsicMessage> {
    ImplGetBill l;
    private Context context;
    private String DeviceID,StationID;
    private ProgressDialog dialog;
    WebServiceHelper webHelper;
    public GetBillTask(Context context,ImplGetBill l){
        this.context=context;
        this.l=l;
        dialog=new ProgressDialog(context);
        GetBasicInfo s=new GetBasicInfo(context);
        DeviceID=s.getDeviceID();
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("正在加载二维码信息");
        dialog.setCancelable(false);
        dialog.show();
    }
    @Override
    protected HsicMessage doInBackground(String... voids) {
        HsicMessage hsicMess=new HsicMessage();
        webHelper=new WebServiceHelper(context);
        hsicMess= webHelper.getUrl(DeviceID,voids[0],voids[1]);
//        Log.e("调用结果", JSONUtils.toJsonWithGson(hsicMess));
        return hsicMess;
    }
    @Override
    protected void onPostExecute(HsicMessage hsicMessage) {
        super.onPostExecute(hsicMessage);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.GetBillTaskEnd(hsicMessage);
    }
}
