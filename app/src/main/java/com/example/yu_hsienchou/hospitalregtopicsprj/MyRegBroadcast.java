package com.example.yu_hsienchou.hospitalregtopicsprj;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Yu-Hsien Chou on 2017/2/10.
 */

public class MyRegBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Work","work");
        //接收參數
        Bundle bundle = intent.getExtras();
        //取得通知管理器
        NotificationManager noMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //當使用者按下通知欄中的通知時要開啟的 Activity
        Intent call = new Intent(context, TodayRegActivity.class);
        //建立待處理意圖
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, call, PendingIntent.FLAG_UPDATE_CURRENT);
        //指定通知出現時要顯示的文字,幾秒後會消失只剩圖示
        //建立通知物件
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(context.getString(R.string.nc_msg_reg_title));
        String str[] = new String[10];
        str[0] =context.getResources().getString(R.string.nc_msg_reg_msg);
        Log.e("test",str[0]);
        builder.setContentText(str[0]);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(pIntent);
        Notification notification = builder.build();
        noMgr.notify(1,notification);
    }
}
