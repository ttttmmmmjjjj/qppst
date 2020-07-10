package com.hsic.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.hsic.tmj.qppst.R;


/**
 * Created by Administrator on 2019/4/2.
 */

public class NewSaleNotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "RepeatReceiver";
    int DEFAULT_NOTIFICATION_ID=1;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        //设置点击通知栏的动作为启动另外一个广播
        Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.
                getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.add_user)
                //设置通知标题
                .setContentTitle("新订单提醒")
                //设置通知内容
                .setContentText("您有新订单了")
//                .setContentIntent(pendingIntent)
                .setSound(Uri.parse("android.resource://com.hsic.tmj.qppst/" + R.raw.sale));
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.getNotification();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(DEFAULT_NOTIFICATION_ID, notification);
    }
}
