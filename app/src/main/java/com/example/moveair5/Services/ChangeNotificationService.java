package com.example.moveair5.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ChangeNotificationService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("Change")
                .putExtra("action", intent.getStringExtra("action")));
    }
}