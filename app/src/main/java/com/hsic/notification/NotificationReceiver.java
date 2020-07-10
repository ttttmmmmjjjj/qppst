package com.hsic.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hsic.tmj.qppst.MainActivity;
import com.hsic.tmj.qppst.SaleListActivity;
import com.hsic.utils.SystemUtils;

/**
 * Created by Administrator on 2019/4/2.
 */

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if(SystemUtils.isAppRunning(context, "ccom.hsic.tmj.qppst")){
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent detailIntent = new Intent(context, SaleListActivity.class);
            Intent[] intents = {mainIntent, detailIntent};
            context.startActivities(intents);
        }
    }
}
