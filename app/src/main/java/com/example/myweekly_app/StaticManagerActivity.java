package com.example.myweekly_app;

import static com.example.myweekly_app.helper.TimeConverters.convertDatabaseTypeToEntryTimeString;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myweekly_app.helper.StaticActivityDatabaseHelper;
import com.example.myweekly_app.model.StaticActivityInfo;
import com.example.myweekly_app.helper.TimeConverters;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StaticManagerActivity extends AppCompatActivity {

    private StaticActivityDatabaseHelper staticActivityDatabaseHelper;
    private Set<String> processedActivityNames = new HashSet<>();

    private LinearLayout staticActivitiesLayoutViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_manager);

        staticActivityDatabaseHelper = new StaticActivityDatabaseHelper(this);

        staticActivitiesLayoutViewer = findViewById(R.id.staticActivitiesLayoutViewer);

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StaticManagerActivity.this, SettingsActivity.class));
            }
        });

        findViewById(R.id.addStaticButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddStaticActivityPopup();
            }
        });

        findViewById(R.id.deleteStaticActivities).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StaticManagerActivity.this);
                builder.setTitle("Delete All Static Activities");
                builder.setMessage("Are you sure you want to delete all static activities?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int deletedCount = staticActivityDatabaseHelper.deleteAllStaticActivities();

                        if (deletedCount > 0) {
                            LinearLayout staticActivitiesLayoutViewer = findViewById(R.id.staticActivitiesLayoutViewer);
                            staticActivitiesLayoutViewer.removeAllViews();

                            Toast.makeText(StaticManagerActivity.this, "All static activities deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(StaticManagerActivity.this, "No static activities found to delete", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("No", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        loadStaticActivities();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStaticActivities();
    }

    private void loadStaticActivities() {
        staticActivitiesLayoutViewer.removeAllViews();

        List<String> activityNames = staticActivityDatabaseHelper.getAllStaticActivityNames();

        for (String activityName : activityNames) {
            addToStaticListLayout(activityName);
        }
    }

    private void showAddStaticActivityPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View addStaticView = getLayoutInflater().inflate(R.layout.add_static_popup, null);
        builder.setView(addStaticView);

        EditText editTextActivityName = addStaticView.findViewById(R.id.ActivityName);

        CheckBox mondayCheck = addStaticView.findViewById(R.id.mondayCheck);
        CheckBox tuesdayCheck = addStaticView.findViewById(R.id.tuesdayCheck);
        CheckBox wednesdayCheck = addStaticView.findViewById(R.id.wednesdayCheck);
        CheckBox thursdayCheck = addStaticView.findViewById(R.id.thursdayCheck);
        CheckBox fridayCheck = addStaticView.findViewById(R.id.fridayCheck);
        CheckBox saturdayCheck = addStaticView.findViewById(R.id.saturdayCheck);
        CheckBox sundayCheck = addStaticView.findViewById(R.id.sundayCheck);

        EditText monStartTime = addStaticView.findViewById(R.id.monStartTime);
        EditText monEndTime = addStaticView.findViewById(R.id.monEndTime);
        EditText tueStartTime = addStaticView.findViewById(R.id.tueStartTime);
        EditText tueEndTime = addStaticView.findViewById(R.id.tueEndTime);
        EditText wedStartTime = addStaticView.findViewById(R.id.wedStartTime);
        EditText wedEndTime = addStaticView.findViewById(R.id.wedEndTime);
        EditText thuStartTime = addStaticView.findViewById(R.id.thuStartTime);
        EditText thuEndTime = addStaticView.findViewById(R.id.thuEndTime);
        EditText friStartTime = addStaticView.findViewById(R.id.friStartTime);
        EditText friEndTime = addStaticView.findViewById(R.id.friEndTime);
        EditText satStartTime = addStaticView.findViewById(R.id.satStartTime);
        EditText satEndTime = addStaticView.findViewById(R.id.satEndTime);
        EditText sunStartTime = addStaticView.findViewById(R.id.sunStartTime);
        EditText sunEndTime = addStaticView.findViewById(R.id.sunEndTime);


        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String activityName = editTextActivityName.getText().toString().trim();

                if (!activityName.isEmpty()) {

                    boolean isValidInput = true;

                    if (mondayCheck.isChecked()) {

                        String monStartTimeInput = monStartTime.getText().toString().trim();
                        String monEndTimeInput = monEndTime.getText().toString().trim();

                        if (TimeConverters.isValidTimeFormat(monStartTimeInput) && TimeConverters.isValidTimeFormat(monEndTimeInput)) {
                            String monStartTimeHHMM = TimeConverters.convertTimeToHHMM(monStartTimeInput);
                            String monEndTimeHHMM = TimeConverters.convertTimeToHHMM(monEndTimeInput);
                            StaticActivityInfo newActivity = new StaticActivityInfo(activityName, "Monday", monStartTimeHHMM, monEndTimeHHMM, 1);
                            staticActivityDatabaseHelper.addStaticActivity(newActivity);
                            Log.d("StaticActivity Adding", "Added static activity: " + activityName + " on day: Monday");
                        } else {
                            isValidInput = false;
                        }
                    }

                    if (tuesdayCheck.isChecked()) {
                        String tueStartTimeInput = tueStartTime.getText().toString().trim();
                        String tueEndTimeInput = tueEndTime.getText().toString().trim();

                        if (TimeConverters.isValidTimeFormat(tueStartTimeInput) && TimeConverters.isValidTimeFormat(tueEndTimeInput)) {
                            String tueStartTimeHHMM = TimeConverters.convertTimeToHHMM(tueStartTimeInput);
                            String tueEndTimeHHMM = TimeConverters.convertTimeToHHMM(tueEndTimeInput);
                            StaticActivityInfo newActivity = new StaticActivityInfo(activityName, "Tuesday", tueStartTimeHHMM, tueEndTimeHHMM, 1);
                            staticActivityDatabaseHelper.addStaticActivity(newActivity);
                            Log.d("StaticActivity Adding", "Added static activity: " + activityName + " on day: Tuesday");
                        } else {
                            isValidInput = false;
                        }
                    }
                    if (wednesdayCheck.isChecked()) {
                        String wedStartTimeInput = wedStartTime.getText().toString().trim();
                        String wedEndTimeInput = wedEndTime.getText().toString().trim();

                        if (TimeConverters.isValidTimeFormat(wedStartTimeInput) && TimeConverters.isValidTimeFormat(wedEndTimeInput)) {
                            String wedStartTimeHHMM = TimeConverters.convertTimeToHHMM(wedStartTimeInput);
                            String wedEndTimeHHMM = TimeConverters.convertTimeToHHMM(wedEndTimeInput);
                            StaticActivityInfo newActivity = new StaticActivityInfo(activityName, "Wednesday", wedStartTimeHHMM, wedEndTimeHHMM, 1);
                            staticActivityDatabaseHelper.addStaticActivity(newActivity);
                            Log.d("StaticActivity Adding", "Added static activity: " + activityName + " on day: Wednesday");
                        } else {
                            isValidInput = false;
                        }
                    }
                    if (thursdayCheck.isChecked()) {
                        String thuStartTimeInput = thuStartTime.getText().toString().trim();
                        String thuEndTimeInput = thuEndTime.getText().toString().trim();

                        if (TimeConverters.isValidTimeFormat(thuStartTimeInput) && TimeConverters.isValidTimeFormat(thuEndTimeInput)) {
                            String thuStartTimeHHMM = TimeConverters.convertTimeToHHMM(thuStartTimeInput);
                            String thuEndTimeHHMM = TimeConverters.convertTimeToHHMM(thuEndTimeInput);
                            StaticActivityInfo newActivity = new StaticActivityInfo(activityName, "Thursday", thuStartTimeHHMM, thuEndTimeHHMM, 1);
                            staticActivityDatabaseHelper.addStaticActivity(newActivity);
                            Log.d("StaticActivity Adding", "Added static activity: " + activityName + " on day: Thursday");
                        } else {
                            isValidInput = false;
                        }
                    }
                    if (fridayCheck.isChecked()) {
                        String friStartTimeInput = friStartTime.getText().toString().trim();
                        String friEndTimeInput = friEndTime.getText().toString().trim();

                        if (TimeConverters.isValidTimeFormat(friStartTimeInput) && TimeConverters.isValidTimeFormat(friEndTimeInput)) {
                            String friStartTimeHHMM = TimeConverters.convertTimeToHHMM(friStartTimeInput);
                            String friEndTimeHHMM = TimeConverters.convertTimeToHHMM(friEndTimeInput);
                            StaticActivityInfo newActivity = new StaticActivityInfo(activityName, "Friday", friStartTimeHHMM, friEndTimeHHMM, 1);
                            staticActivityDatabaseHelper.addStaticActivity(newActivity);
                            Log.d("StaticActivity Adding", "Added static activity: " + activityName + " on day: Friday");
                        } else {
                            isValidInput = false;
                        }
                    }
                    if (saturdayCheck.isChecked()) {
                        String satStartTimeInput = satStartTime.getText().toString().trim();
                        String satEndTimeInput = satEndTime.getText().toString().trim();

                        if (TimeConverters.isValidTimeFormat(satStartTimeInput) && TimeConverters.isValidTimeFormat(satEndTimeInput)) {
                            String satStartTimeHHMM = TimeConverters.convertTimeToHHMM(satStartTimeInput);
                            String satEndTimeHHMM = TimeConverters.convertTimeToHHMM(satEndTimeInput);
                            StaticActivityInfo newActivity = new StaticActivityInfo(activityName, "Saturday", satStartTimeHHMM, satEndTimeHHMM, 1);
                            staticActivityDatabaseHelper.addStaticActivity(newActivity);
                            Log.d("StaticActivity Adding", "Added static activity: " + activityName + " on day: Saturday");
                        } else {
                            isValidInput = false;
                        }
                    }
                    if (sundayCheck.isChecked()) {
                        String sunStartTimeInput = sunStartTime.getText().toString().trim();
                        String sunEndTimeInput = sunEndTime.getText().toString().trim();

                        if (TimeConverters.isValidTimeFormat(sunStartTimeInput) && TimeConverters.isValidTimeFormat(sunEndTimeInput)) {
                            String sunStartTimeHHMM = TimeConverters.convertTimeToHHMM(sunStartTimeInput);
                            String sunEndTimeHHMM = TimeConverters.convertTimeToHHMM(sunEndTimeInput);
                            StaticActivityInfo newActivity = new StaticActivityInfo(activityName, "Sunday", sunStartTimeHHMM, sunEndTimeHHMM, 1);
                            staticActivityDatabaseHelper.addStaticActivity(newActivity);
                            Log.d("StaticActivity Adding", "Added static activity: " + activityName + ". On day: Sunday");
                        } else {
                            isValidInput = false;
                        }
                    }

                    addToStaticListLayout(activityName);

                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String getDayOfWeek(int dayIndex) {
        String[] daysOfWeek = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        return daysOfWeek[dayIndex];
    }

    private void addStaticActivityToDatabase(StaticActivityInfo activity) {
        long result = staticActivityDatabaseHelper.addStaticActivity(activity);

        if (result != -1) {
            activity.setId(result);
        } else {
            Toast.makeText(this, "Failed to add activity", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToStaticListLayout(String activityName) {
        String daysText = staticActivityDatabaseHelper.getSelectedDaysStringForActivity(activityName);
        Button button = new Button(this);
        button.setText(activityName + "\n" + daysText);
        button.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        button.setOnClickListener(v -> editStaticActivity(activityName));
        staticActivitiesLayoutViewer.addView(button);
    }

    private void editStaticActivity(String activityName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View editStaticView = getLayoutInflater().inflate(R.layout.edit_static_activity_popup, null);
        builder.setView(editStaticView);

        EditText editTextActivityName = editStaticView.findViewById(R.id.EditActivityName);
        editTextActivityName.setText(activityName);

        CheckBox mondayEditCheck = editStaticView.findViewById(R.id.editMondayCheck);
        CheckBox tuesdayEditCheck = editStaticView.findViewById(R.id.editTuesdayCheck);
        CheckBox wednesdayEditCheck = editStaticView.findViewById(R.id.editWednesdayCheck);
        CheckBox thursdayEditCheck = editStaticView.findViewById(R.id.editThursdayCheck);
        CheckBox fridayEditCheck = editStaticView.findViewById(R.id.editFridayCheck);
        CheckBox saturdayEditCheck = editStaticView.findViewById(R.id.editSaturdayCheck);
        CheckBox sundayEditCheck = editStaticView.findViewById(R.id.editSundayCheck);

        EditText monEditStartTime = editStaticView.findViewById(R.id.monEditStartTime);
        EditText monEditEndTime = editStaticView.findViewById(R.id.monEditEndTime);
        EditText tueEditStartTime = editStaticView.findViewById(R.id.tueEditStartTime);
        EditText tueEditEndTime = editStaticView.findViewById(R.id.tueEditEndTime);
        EditText wedEditStartTime = editStaticView.findViewById(R.id.wedEditStartTime);
        EditText wedEditEndTime = editStaticView.findViewById(R.id.wedEditEndTime);
        EditText thuEditStartTime = editStaticView.findViewById(R.id.thuEditStartTime);
        EditText thuEditEndTime = editStaticView.findViewById(R.id.thuEditEndTime);
        EditText friEditStartTime = editStaticView.findViewById(R.id.friEditStartTime);
        EditText friEditEndTime = editStaticView.findViewById(R.id.friEditEndTime);
        EditText satEditStartTime = editStaticView.findViewById(R.id.satEditStartTime);
        EditText satEditEndTime = editStaticView.findViewById(R.id.satEditEndTime);
        EditText sunEditStartTime = editStaticView.findViewById(R.id.sunEditStartTime);
        EditText sunEditEndTime = editStaticView.findViewById(R.id.sunEditEndTime);

        List<StaticActivityInfo> activities = staticActivityDatabaseHelper.getStaticActivitiesByName(activityName);

        String startTime;
        for (StaticActivityInfo activity : activities) {
            String dayOfWeek = activity.getDay();
            startTime = activity.getStart();
            String endTime = activity.getEnd();
            Log.d("StaticActivity", "Found static activity: " + activityName + " Day of week: " + dayOfWeek);

            switch (dayOfWeek) {
                case "Monday":
                    mondayEditCheck.setChecked(true);
                    mondayEditCheck.setText(convertDatabaseTypeToEntryTimeString(startTime));
                    monEditEndTime.setText(convertDatabaseTypeToEntryTimeString(endTime));
                    break;
                case "Tuesday":
                    tuesdayEditCheck.setChecked(true);
                    tueEditStartTime.setText(convertDatabaseTypeToEntryTimeString(startTime));
                    tueEditEndTime.setText(convertDatabaseTypeToEntryTimeString(endTime));
                    break;
                case "Wednesday":
                    wednesdayEditCheck.setChecked(true);
                    wedEditStartTime.setText(convertDatabaseTypeToEntryTimeString(startTime));
                    wedEditEndTime.setText(convertDatabaseTypeToEntryTimeString(endTime));
                    break;
                case "Thursday":
                    thursdayEditCheck.setChecked(true);
                    thuEditStartTime.setText(convertDatabaseTypeToEntryTimeString(startTime));
                    thuEditEndTime.setText(convertDatabaseTypeToEntryTimeString(endTime));
                    break;
                case "Friday":
                    fridayEditCheck.setChecked(true);
                    friEditStartTime.setText(convertDatabaseTypeToEntryTimeString(startTime));
                    friEditEndTime.setText(convertDatabaseTypeToEntryTimeString(endTime));
                    break;
                case "Saturday":
                    saturdayEditCheck.setChecked(true);
                    satEditStartTime.setText(convertDatabaseTypeToEntryTimeString(startTime));
                    satEditEndTime.setText(convertDatabaseTypeToEntryTimeString(endTime));
                    break;
                case "Sunday":
                    sundayEditCheck.setChecked(true);
                    sunEditStartTime.setText(convertDatabaseTypeToEntryTimeString(startTime));
                    sunEditEndTime.setText(convertDatabaseTypeToEntryTimeString(endTime));
                    break;
            }
        }

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String activityName = editTextActivityName.getText().toString().trim();

                if (!activityName.isEmpty()) {

                    boolean isValidInput = true;

                    if (mondayEditCheck.isChecked()) {

                        String monStartTimeInput = monEditStartTime.getText().toString().trim();
                        String monEndTimeInput = monEditEndTime.getText().toString().trim();

                        if (TimeConverters.isValidTimeFormat(monStartTimeInput) && TimeConverters.isValidTimeFormat(monEndTimeInput)) {
                            String monStartTimeHHMM = TimeConverters.convertTimeToHHMM(monStartTimeInput);
                            String monEndTimeHHMM = TimeConverters.convertTimeToHHMM(monEndTimeInput);
                            StaticActivityInfo newActivity = new StaticActivityInfo(activityName, "Monday", monStartTimeHHMM, monEndTimeHHMM, 1);
                            staticActivityDatabaseHelper.addStaticActivity(newActivity);
                        } else {
                            isValidInput = false;
                        }
                    }

                    if (tuesdayEditCheck.isChecked()) {
                        String tueStartTimeInput = tueEditStartTime.getText().toString().trim();
                        String tueEndTimeInput = tueEditEndTime.getText().toString().trim();

                        if (TimeConverters.isValidTimeFormat(tueStartTimeInput) && TimeConverters.isValidTimeFormat(tueEndTimeInput)) {
                            String tueStartTimeHHMM = TimeConverters.convertTimeToHHMM(tueStartTimeInput);
                            String tueEndTimeHHMM = TimeConverters.convertTimeToHHMM(tueEndTimeInput);
                            StaticActivityInfo newActivity = new StaticActivityInfo(activityName, "Tuesday", tueStartTimeHHMM, tueEndTimeHHMM, 1);
                            staticActivityDatabaseHelper.addStaticActivity(newActivity);
                        } else {
                            isValidInput = false;
                        }
                    }
                    if (wednesdayEditCheck.isChecked()) {
                        String wedStartTimeInput = wedEditStartTime.getText().toString().trim();
                        String wedEndTimeInput = wedEditEndTime.getText().toString().trim();

                        if (TimeConverters.isValidTimeFormat(wedStartTimeInput) && TimeConverters.isValidTimeFormat(wedEndTimeInput)) {
                            String wedStartTimeHHMM = TimeConverters.convertTimeToHHMM(wedStartTimeInput);
                            String wedEndTimeHHMM = TimeConverters.convertTimeToHHMM(wedEndTimeInput);
                            StaticActivityInfo newActivity = new StaticActivityInfo(activityName, "Wednesday", wedStartTimeHHMM, wedEndTimeHHMM, 1);
                            staticActivityDatabaseHelper.addStaticActivity(newActivity);
                        } else {
                            isValidInput = false;
                        }
                    }
                    if (thursdayEditCheck.isChecked()) {
                        String thuStartTimeInput = thuEditStartTime.getText().toString().trim();
                        String thuEndTimeInput = thuEditEndTime.getText().toString().trim();

                        if (TimeConverters.isValidTimeFormat(thuStartTimeInput) && TimeConverters.isValidTimeFormat(thuEndTimeInput)) {
                            String thuStartTimeHHMM = TimeConverters.convertTimeToHHMM(thuStartTimeInput);
                            String thuEndTimeHHMM = TimeConverters.convertTimeToHHMM(thuEndTimeInput);
                            StaticActivityInfo newActivity = new StaticActivityInfo(activityName, "Thursday", thuStartTimeHHMM, thuEndTimeHHMM, 1);
                            staticActivityDatabaseHelper.addStaticActivity(newActivity);
                        } else {
                            isValidInput = false;
                        }
                    }
                    if (fridayEditCheck.isChecked()) {
                        String friStartTimeInput = friEditStartTime.getText().toString().trim();
                        String friEndTimeInput = friEditEndTime.getText().toString().trim();

                        if (TimeConverters.isValidTimeFormat(friStartTimeInput) && TimeConverters.isValidTimeFormat(friEndTimeInput)) {
                            String friStartTimeHHMM = TimeConverters.convertTimeToHHMM(friStartTimeInput);
                            String friEndTimeHHMM = TimeConverters.convertTimeToHHMM(friEndTimeInput);
                            StaticActivityInfo newActivity = new StaticActivityInfo(activityName, "Friday", friStartTimeHHMM, friEndTimeHHMM, 1);
                            staticActivityDatabaseHelper.addStaticActivity(newActivity);
                        } else {
                            isValidInput = false;
                        }
                    }
                    if (saturdayEditCheck.isChecked()) {
                        String satStartTimeInput = satEditStartTime.getText().toString().trim();
                        String satEndTimeInput = satEditEndTime.getText().toString().trim();

                        if (TimeConverters.isValidTimeFormat(satStartTimeInput) && TimeConverters.isValidTimeFormat(satEndTimeInput)) {
                            String satStartTimeHHMM = TimeConverters.convertTimeToHHMM(satStartTimeInput);
                            String satEndTimeHHMM = TimeConverters.convertTimeToHHMM(satEndTimeInput);
                            StaticActivityInfo newActivity = new StaticActivityInfo(activityName, "Saturday", satStartTimeHHMM, satEndTimeHHMM, 1);
                            staticActivityDatabaseHelper.addStaticActivity(newActivity);
                        } else {
                            isValidInput = false;
                        }
                    }
                    if (sundayEditCheck.isChecked()) {
                        String sunStartTimeInput = sunEditStartTime.getText().toString().trim();
                        String sunEndTimeInput = sunEditEndTime.getText().toString().trim();

                        if (TimeConverters.isValidTimeFormat(sunStartTimeInput) && TimeConverters.isValidTimeFormat(sunEndTimeInput)) {
                            String sunStartTimeHHMM = TimeConverters.convertTimeToHHMM(sunStartTimeInput);
                            String sunEndTimeHHMM = TimeConverters.convertTimeToHHMM(sunEndTimeInput);
                            StaticActivityInfo newActivity = new StaticActivityInfo(activityName, "Sunday", sunStartTimeHHMM, sunEndTimeHHMM, 1);
                            staticActivityDatabaseHelper.addStaticActivity(newActivity);
                        } else {
                            isValidInput = false;
                        }
                    }
                }
            }
        });

        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                staticActivityDatabaseHelper.deleteStaticActivitiesByName(activityName);

                loadStaticActivities();
                Toast.makeText(StaticManagerActivity.this, "Static activities deleted", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNeutralButton("Cancel", null);
        builder.create().show();
    }
}