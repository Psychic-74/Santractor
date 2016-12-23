package com.nick.santractor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by Nick on 12/23/2016.
 */

public class NotificationListener extends NotificationListenerService {
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String appName = sbn.getPackageName();
        String appTitle;
        Bundle notiExtras = sbn.getNotification().extras;
        if (appName.matches("com.saavn.android")){
            Log.i("santractor", "Saavn notification found");
            appTitle = notiExtras.getString("android.title");
            Log.i("santractor", "Found song name as: "+appTitle);
            Intent sendMsg = new Intent("sendMsg");
            sendMsg.putExtra("appTitle", appTitle);
            LocalBroadcastManager.getInstance(context).sendBroadcast(sendMsg);
        }
        else {
            Log.e("santractor", "Saavn notification not found");
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.e("santractor", "Notification removed");
    }
}
