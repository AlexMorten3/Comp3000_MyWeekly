package com.example.myweekly_app.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, MyScheduledService.class);
        context.startService(serviceIntent);
    }
}

