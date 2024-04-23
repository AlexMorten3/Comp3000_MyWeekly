package com.example.myweekly_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myweekly_app.helper.ActivityDatabaseHelper;
import com.example.myweekly_app.helper.GeneratedActivityDatabaseHelper;
import com.example.myweekly_app.helper.NextWeeklyDatabaseHelper;
import com.example.myweekly_app.model.UserInfo;
import com.example.myweekly_app.status.SharedPreferenceManager;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.User;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchNotifications;
    private Switch switchIncludeStatic;
    private Switch switchIncludeRecommendations;
    private Switch switchColorChanging;

    String appID = "application-0-csvmn";
    private App app;
    private TextView displayUserTextView;
    private boolean isButtonColorStatic;
    private UserInfo userInfo;

    private Button saveSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Realm.init(this);

        app = new App(new AppConfiguration.Builder(appID).build());

        switchNotifications = findViewById(R.id.notifications_switch);
        switchIncludeStatic = findViewById(R.id.enable_statics_switch);
        switchIncludeRecommendations = findViewById(R.id.activity_recommendations_switch);
        switchColorChanging = findViewById(R.id.enable_dynamic_buttons_switch);

        displayUserTextView = findViewById(R.id.displayUser);

        saveSettingsButton = findViewById(R.id.saveSettings);

        loadSettings();

        findViewById(R.id.saveSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Switch notificationsSwitch = findViewById(R.id.notifications_switch);
                boolean isNotificationsEnabled = notificationsSwitch.isChecked();

                Switch staticsSwitch = findViewById(R.id.enable_statics_switch);
                boolean includeStatics = staticsSwitch.isChecked();

                Switch recommendationsSwitch = findViewById(R.id.activity_recommendations_switch);
                boolean includeRecommendations = recommendationsSwitch.isChecked();

                Switch colorChangerSwitch = findViewById(R.id.enable_dynamic_buttons_switch);
                boolean includeColorChanging = colorChangerSwitch.isChecked();

                editor.putBoolean("isNotifications", isNotificationsEnabled);
                editor.putBoolean("includeStatic", includeStatics);
                editor.putBoolean("includeRecommendations", includeRecommendations);
                editor.putBoolean("isColorChanger", includeColorChanging);

                editor.apply();

                saveSettingsButton.setBackgroundColor(Color.parseColor("#2ab327"));

                Toast.makeText(SettingsActivity.this, "Settings saved", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.homeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
            }
        });

        findViewById(R.id.deleteWeekly).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPreferenceManager.isActiveWeekly()) {
                    showDeleteConfirmationDialog();
                } else {
                    Toast.makeText(SettingsActivity.this, "No active weekly to delete", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.staticActivitiesButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, StaticManagerActivity.class));
            }
        });

        Switch notificationsSwitch = findViewById(R.id.notifications_switch);
        notificationsSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateSaveButtonBackground();
            }
        });

        Switch staticsSwitch = findViewById(R.id.enable_statics_switch);
        staticsSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateSaveButtonBackground();
            }
        });

        Switch recommendationsSwitch = findViewById(R.id.activity_recommendations_switch);
        recommendationsSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateSaveButtonBackground();
            }
        });

        Switch colorChangerSwitch = findViewById(R.id.enable_dynamic_buttons_switch);
        colorChangerSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateSaveButtonBackground();
            }
        });

        findViewById(R.id.logOutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserInfo userInfo = new UserInfo();
                userInfo.signOut();
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            }
        });

        displayUsername();
    }

    private void displayUsername() {
        User currentUser = app.currentUser();
        if (currentUser != null) {
            String username = currentUser.getProfile().getEmail();
            displayUserTextView.setText(username);
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete your current Weekly?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferenceManager.setIsActive(false);
                SharedPreferenceManager.setIsLoaded(false);
                SharedPreferenceManager.setIsGenerated(false);

                deleteAllActivities();

                Toast.makeText(SettingsActivity.this, "Weekly deleted", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void deleteAllActivities() {
        ActivityDatabaseHelper.deleteAllActivities(this);
        GeneratedActivityDatabaseHelper.deleteAllActivities(this);
    }

    private void loadSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);

        boolean isNotificationsEnabled = sharedPreferences.getBoolean("isNotifications", true);
        boolean isIncludeStaticEnabled = sharedPreferences.getBoolean("includeStatic", true);
        boolean isIncludeRecommendationsEnabled = sharedPreferences.getBoolean("includeRecommendations", true);
        boolean isColorChanger = sharedPreferences.getBoolean("isColorChanger", true);

        switchNotifications.setChecked(isNotificationsEnabled);
        switchIncludeStatic.setChecked(isIncludeStaticEnabled);
        switchIncludeRecommendations.setChecked(isIncludeRecommendationsEnabled);
        switchColorChanging.setChecked(isColorChanger);
    }

    private void updateSaveButtonBackground() {

        if (switchNotifications.isChecked() || switchIncludeStatic.isChecked() || switchIncludeRecommendations.isChecked()) {
            saveSettingsButton.setBackgroundColor(Color.parseColor("#de382c"));
        } else {
            saveSettingsButton.setBackgroundColor(Color.TRANSPARENT); // Change to your default color
        }
    }
}