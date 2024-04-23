package com.example.myweekly_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myweekly_app.broadcast.AlarmReceiver;
import com.example.myweekly_app.fragment.MyWeeklyFragment;
import com.example.myweekly_app.fragment.ButtonToCreateFragment;
import com.example.myweekly_app.status.SharedPreferenceManager;

import java.util.Calendar;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;

public class HomeActivity extends AppCompatActivity {

    private SharedPreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        preferenceManager = new SharedPreferenceManager(getApplicationContext());

        Button generateNextWeeklyButton = findViewById(R.id.createWeeklyButton);
        Button settingsButton = findViewById(R.id.settingsButton);
        Button editButton = findViewById(R.id.edit);

        generateNextWeeklyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, GeneratorActivity.class);
                intent.putExtra("NEXT_WEEKLY_GENERATOR_MODE", true);
                startActivity(intent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, GeneratorActivity.class);
                intent.putExtra("EDIT_MODE", true);
                startActivity(intent);
            }
        });

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);

        boolean isCreated = preferenceManager.isCreatedWeekly();
        boolean isActive = preferenceManager.isActiveWeekly();
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        if (isCreated) {
            generateNextWeeklyButton.setText("Edit Next Weekly");
        }

        if (isWeekend(dayOfWeek)) {
            generateNextWeeklyButton.setVisibility(View.VISIBLE);
        } else {
            generateNextWeeklyButton.setVisibility(View.INVISIBLE);
        }

        loadFragment(isActive);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isActive = preferenceManager.isActiveWeekly();
        loadFragment(isActive);
    }

    private void loadFragment(boolean isActive) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isActive) {
            fragmentTransaction.replace(R.id.weeklyFragmentContainer, new MyWeeklyFragment());
        } else {
            fragmentTransaction.replace(R.id.weeklyFragmentContainer, new ButtonToCreateFragment());
        }

        fragmentTransaction.commit();
    }

    private boolean isWeekend(int dayOfWeek) {
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }
}