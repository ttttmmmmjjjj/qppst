package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplContractTask;
import com.hsic.web.WebServiceHelper;

/**
 * Created by Administrator on 2019/8/27.
 */

public class UpContractTask extends AsyncTask<String,Void,HsicMessage> {
    ImplContractTask l;
    private Context context;
    private String DeviceID,StationID;
    private ProgressDialog dialog;
    WebServiceHelper webHelper;
    public UpContractTask(Context context,ImplContractTask l){
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
        return hsicMess;
    }
    @Override
    protected void onPostExecute(HsicMessage hsicMessage) {
        super.onPostExecute(hsicMessage);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.ContractTaskEnd(hsicMessage);
    }
}
