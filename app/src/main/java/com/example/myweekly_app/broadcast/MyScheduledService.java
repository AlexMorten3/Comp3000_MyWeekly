package com.example.myweekly_app.broadcast;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import javax.annotation.Nullable;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.myweekly_app.R;
import com.example.myweekly_app.helper.ActivityDatabaseHelper;
import com.example.myweekly_app.helper.NextWeeklyDatabaseHelper;
import com.example.myweekly_app.model.ActivityInfo;
import com.example.myweekly_app.status.SharedPreferenceManager;

import java.util.Calendar;
import java.util.List;

public class MyScheduledService extends Service {

    private NextWeeklyDatabaseHelper nextWeeklyDatabaseHelper;
    private ActivityDatabaseHelper activityDatabaseHelper;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (dayOfWeek == Calendar.SUNDAY && hourOfDay == 0) {
            Log.d("MyScheduledService", "Executing task at midnight on Sunday");

            ActivityDatabaseHelper.deleteAllActivities(this);

            if (SharedPreferenceManager.isCreated()) {
                SharedPreferenceManager.setIsActive(true);
                SharedPreferenceManager.setIsCreated(false);

                nextWeeklyDatabaseHelper = new NextWeeklyDatabaseHelper(this);
                activityDatabaseHelper = new ActivityDatabaseHelper(this);

                List<ActivityInfo> activities = nextWeeklyDatabaseHelper.getAllActivities();
                for (ActivityInfo activity : activities) {
                    activityDatabaseHelper.addActivity(activity);
                }

                NextWeeklyDatabaseHelper.deleteAllActivities(this);
            }
        }

        if (dayOfWeek == Calendar.SUNDAY && hourOfDay == 14) {
            Log.d("MyScheduledService", "Executing task at 14:00 on Sunday");

            sendNotification();
        }

        stopSelf();
        return START_NOT_STICKY;
    }

    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Generate your next weekly now!")
                .setContentText("Today is the last day to generate next weeks Weekly")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }

    private void scheduleNotificationAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

