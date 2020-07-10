package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplGetDevicesTask;
import com.hsic.web.WebServiceHelper;

/**
 * Created by Administrator on 2018/8/27.
 */

public class GetDevicesTask extends AsyncTask<Void,Void,HsicMessage> {
    ImplGetDevicesTask l;
    private Context context;
    WebServiceHelper webHelper;
    private String DeviceID,StationID;
    private ProgressDialog dialog;
    public GetDevicesTask(Context context,ImplGetDevicesTask l){
        this.l=l;
        this.context=context;
        dialog=new ProgressDialog(context);
        GetBasicInfo s=new GetBasicInfo(context);
        DeviceID=s.getDeviceID();
        StationID=s.getStationID();
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("正在下载配件信息");
        dialog.setCancelable(false);
        dialog.show();
    }
    @Override
    protected HsicMessage doInBackground(Void... voids) {
        HsicMessage hsicMess=new HsicMessage();
        webHelper=new WebServiceHelper(context);
        hsicMess= webHelper.getDevice(DeviceID,StationID);
//        Log.e("调用结果", JSONUtils.toJsonWithGson(hsicMess));
        return hsicMess;
    }
    @Override
    protected void onPostExecute(HsicMessage hsicMessage) {
        super.onPostExecute(hsicMessage);
        dialog.setCancelable(true);
        dialog.dismiss();
        l.GetDevicesTaskEnd(hsicMessage);
    }
}
