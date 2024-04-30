package com.example.myweekly_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myweekly_app.helper.GeneratedActivityDatabaseHelper;
import com.example.myweekly_app.helper.MongoActivitiesDatabaseHelper;
import com.example.myweekly_app.helper.NextWeeklyDatabaseHelper;
import com.example.myweekly_app.model.GeneratedActivityInfo;
import com.example.myweekly_app.model.MongoActivity;
import com.example.myweekly_app.model.TimeSlot;
import com.example.myweekly_app.mongo.MyApiClient;
import com.example.myweekly_app.mongo.RetrofitInterface;
import com.example.myweekly_app.mongo.WeeklyMongoFunctions;
import com.example.myweekly_app.model.ActivityInfo;
import com.example.myweekly_app.helper.ActivityDatabaseHelper;
import com.example.myweekly_app.helper.StaticActivityDatabaseHelper;
import com.example.myweekly_app.helper.TimeConverters;
import com.example.myweekly_app.status.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GeneratorActivity extends AppCompatActivity {

    private ActivityDatabaseHelper activityDatabaseHelper;
    private StaticActivityDatabaseHelper staticActivityDatabaseHelper;
    private GeneratedActivityDatabaseHelper generatedActivityDatabaseHelper;
    private MongoActivitiesDatabaseHelper mongoActivitiesDatabaseHelper;
    private NextWeeklyDatabaseHelper nextWeeklyDatabaseHelper;

    private static final int MINUTES_IN_DAY = 1440;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);

        boolean isInEditMode = getIntent().getBooleanExtra("EDIT_MODE", false);
        boolean isInNextWeekMode = getIntent().getBooleanExtra("NEXT_WEEKLY_GENERATOR_MODE", false);

        if (isInEditMode) {
            TextView titleTextView = findViewById(R.id.generator_title);
            if (titleTextView != null) {
                titleTextView.setText("Edit My Weekly");
            }
        } else if (isInNextWeekMode) {
            TextView titleTextView = findViewById(R.id.generator_title);
            if (titleTextView != null) {
                titleTextView.setText("Generate Next Weekly");
            }
        }

        activityDatabaseHelper = new ActivityDatabaseHelper(this);
        staticActivityDatabaseHelper = new StaticActivityDatabaseHelper(this);
        generatedActivityDatabaseHelper = new GeneratedActivityDatabaseHelper(this);
        nextWeeklyDatabaseHelper = new NextWeeklyDatabaseHelper(this);

        findViewById(R.id.finishCreator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferenceManager.setIsActive(true);

                if (!isInEditMode && !isInNextWeekMode) {
                    WeeklyMongoFunctions.main(GeneratorActivity.this);
                }

                startActivity(new Intent(GeneratorActivity.this, HomeActivity.class));
                finish();
            }
        });

        findViewById(R.id.addActivityButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddActivityPopup(isInEditMode, isInNextWeekMode);
            }
        });

        if (SharedPreferenceManager.isActivityRecommendationsIncluded()) {
            if (!SharedPreferenceManager.isGenerated()){
                WeeklyMongoFunctions weeklyFunctions = new WeeklyMongoFunctions(this);
                weeklyFunctions.retrieveActivitiesFromMongoDB();

                Log.d("Generator", "Beginning Generation");
                generateActivities(isInEditMode, isInNextWeekMode);
                SharedPreferenceManager.setIsGenerated(true);
            }
        }

        loadGeneratedActivities(isInEditMode, isInNextWeekMode);
        loadStaticActivities(isInEditMode, isInNextWeekMode);

    }

    private void loadGeneratedActivities(boolean isInEditMode, boolean isInNextWeekMode) {

        if (SharedPreferenceManager.isActivityRecommendationsIncluded()) {

            int type = 0;

            List<ActivityInfo> activities = generatedActivityDatabaseHelper.getAllGeneratedActivities();

            for (ActivityInfo activity : activities) {
                Log.d("Activity to add", "Generator - Name: " + activity.getName() + " Day: " + activity.getDay() + " Start: " + activity.getStart() + " End: " + activity.getEnd());
                addToDayLayout(activity, type);

                if (SharedPreferenceManager.isGenerated()) {
                    addActivityToDatabase(activity.getName(), activity.getDay(), activity.getStart(), activity.getEnd(), isInEditMode, isInNextWeekMode);
                }
            }
        }
    }


    private void loadStaticActivities(boolean isInEditMode, boolean isInNextWeekMode) {

        if (SharedPreferenceManager.isStaticActivitiesIncluded()) {

            int type = 1;

            List<ActivityInfo> activities = staticActivityDatabaseHelper.getAllStaticActivities();

            for (ActivityInfo activity : activities) {
                Log.d("Activity to add", "Static - Name: " + activity.getName() + " Day: " + activity.getDay() + " Start: " + activity.getStart() + " End: " + activity.getEnd());
                addToDayLayout(activity, type);

                addActivityToDatabase(activity.getName(), activity.getDay(), activity.getStart(), activity.getEnd(), isInEditMode, isInNextWeekMode);
            }

            SharedPreferenceManager.setIsLoaded(true);
        }
    }

    public void generateActivities(boolean isInEditMode, boolean isInNextWeekMode) {

        List<TimeSlot> availableTimeSlots = null;
        if (SharedPreferenceManager.isStaticActivitiesIncluded()) {
            List<ActivityInfo> activities = staticActivityDatabaseHelper.getAllStaticActivities();
            availableTimeSlots = calculateAvailableTimeSlots(activities);
        }
        if (!isInEditMode) {
            generateMongoActivities(availableTimeSlots, isInEditMode, isInNextWeekMode);
        }
    }

    private void addGeneratedActivityToDatabase(GeneratedActivityInfo activity, boolean isInEditMode, boolean isInNextWeekMode) {
        if (isInNextWeekMode) {
            if (nextWeeklyDatabaseHelper != null) {
                ActivityInfo generatedActivity = new ActivityInfo(activity.getName(), activity.getState(), activity.getDay(), activity.getStart(), activity.getEnd());
                nextWeeklyDatabaseHelper.addActivity(generatedActivity);
            }
        } else {
            if (generatedActivityDatabaseHelper != null) {
                GeneratedActivityInfo generatedActivity = new GeneratedActivityInfo(activity.getName(), activity.getDay(), activity.getStart(), activity.getEnd(), activity.getState());
                generatedActivityDatabaseHelper.addGeneratedActivity(generatedActivity);
            }
        }
    }

    private void addActivityToDatabase(String activityName, String dayOfWeek, String startTime, String endTime, boolean isInEditMode, boolean isInNextWeekMode) {
        int state = 1;
        ActivityInfo activityInfo = new ActivityInfo(activityName, state, dayOfWeek, startTime, endTime);

        if (isInNextWeekMode) {

            long result = nextWeeklyDatabaseHelper.addActivity(activityInfo);
            if (result != -1) {
                activityInfo.setId(result);
                Log.d("Adder Next Week", "added to db");
            } else {
                Log.d("Adder Next Week", "Failed to add");
            }
        } else if (isInEditMode) {

            return;

        } else {
            if (!SharedPreferenceManager.isLoaded()) {
                long result = activityDatabaseHelper.addActivity(activityInfo);
                if (result != -1) {
                    activityInfo.setId(result);
                    Log.d("Adder", "added to db");
                }else {
                    Log.d("Adder", "Failed to add");
                }
            }
        }
    }

    private List<GeneratedActivityInfo> generateMongoActivities(List<TimeSlot> availableTimeSlots, boolean isInEditMode, boolean isInNextWeekMode) {
        List<GeneratedActivityInfo> generatedActivities = new ArrayList<>();

        if (availableTimeSlots.isEmpty()) {
            Log.d("Generating activities", "Available time slots empty");
            return generatedActivities;
        }

        Log.d("Generating activities", "Beginning generation");

        mongoActivitiesDatabaseHelper = new MongoActivitiesDatabaseHelper(this);

        for (TimeSlot timeSlot : availableTimeSlots) {
            int startTime = timeSlot.getStartTime();
            int endTime = timeSlot.getEndTime();
            String day = timeSlot.getDay();
            int duration = endTime - startTime;
            boolean isWeekend = day.equals("Saturday") || day.equals("Sunday");

            int timeOfDay = TimeConverters.getTimeOfDay(startTime);

            String durationType;
            if (duration >= 240) {
                durationType = "Long";
            } else if (duration >= 120) {
                durationType = "Medium";
            } else {
                durationType = "Short";
            }

            List<MongoActivity> validActivities = mongoActivitiesDatabaseHelper.getMongoActivitiesByFilter(durationType, isWeekend, timeOfDay);

            if (!validActivities.isEmpty()) {
                Random random = new Random();
                MongoActivity randomMongoActivity = validActivities.get(random.nextInt(validActivities.size()));

                String startTimeHHMM = TimeConverters.convertMinutesToHHMM(startTime);
                String endTimeHHMM = TimeConverters.convertMinutesToHHMM(endTime);

                GeneratedActivityInfo generatedActivity = new GeneratedActivityInfo(randomMongoActivity.getName(), day, startTimeHHMM, endTimeHHMM, 1);
                generatedActivities.add(generatedActivity);

                Log.d("Generated Activity", "Generated: " + randomMongoActivity.getName() + " on " + day + " from " + startTimeHHMM + " to " + endTimeHHMM);

                addGeneratedActivityToDatabase(generatedActivity, isInEditMode, isInNextWeekMode);
            }
        }

        return generatedActivities;
    }

    private List<TimeSlot> calculateAvailableTimeSlots(List<ActivityInfo> activities) {
        Map<String, List<TimeSlot>> occupiedTimeSlotsByDay = new HashMap<>();
        final int SHORT_ACTIVITY = 40;
        final int MEDIUM_ACTIVITY = 120;
        final int LONG_ACTIVITY = 240;
        final int BUFFER_TIME = 30;
        final int MIN_START_TIME = 540;
        final int MAX_END_TIME = 1380;

        for (ActivityInfo activity : activities) {
            String day = activity.getDay();
            int startTime = TimeConverters.convertStringTimeToMinutes(activity.getStart());
            int endTime = TimeConverters.convertStringTimeToMinutes(activity.getEnd());

            TimeSlot occupiedSlot = new TimeSlot(startTime, endTime, day);
            occupiedTimeSlotsByDay.putIfAbsent(day, new ArrayList<>());
            occupiedTimeSlotsByDay.get(day).add(occupiedSlot);
        }

        List<TimeSlot> availableTimeSlots = new ArrayList<>();

        for (String day : occupiedTimeSlotsByDay.keySet()) {
            List<TimeSlot> occupiedSlots = occupiedTimeSlotsByDay.get(day);
            Collections.sort(occupiedSlots, Comparator.comparingInt(TimeSlot::getStartTime));

            int lastEndTime = MIN_START_TIME;

            for (TimeSlot slot : occupiedSlots) {
                int slotStartTime = slot.getStartTime();
                int slotEndTime = slot.getEndTime();

                if (slotStartTime - lastEndTime >= BUFFER_TIME * 2) {
                    int start = Math.max(lastEndTime + BUFFER_TIME, MIN_START_TIME);
                    int end = Math.min(slotStartTime - BUFFER_TIME, MAX_END_TIME);
                    int differenceInMinutes = slotEndTime - slotStartTime;
                    if (differenceInMinutes > 60) {
                        addAvailableSlots(availableTimeSlots, start, end, day, SHORT_ACTIVITY, MEDIUM_ACTIVITY, LONG_ACTIVITY, BUFFER_TIME);
                        Log.d("Available slot", "Day: " + day + ", Start: " + start + ", End: " + end);
                    }
                }
                lastEndTime = slotEndTime;
            }

            if (MAX_END_TIME - lastEndTime >= BUFFER_TIME * 2) {
                int start = Math.max(lastEndTime + BUFFER_TIME, MIN_START_TIME);
                int end = Math.min(MAX_END_TIME, MAX_END_TIME);
                addAvailableSlots(availableTimeSlots, start, end, day, SHORT_ACTIVITY, MEDIUM_ACTIVITY, LONG_ACTIVITY, BUFFER_TIME);
                Log.d("Available slot", "Day: " + day + ", Start: " + start + ", End: " + end);
            }
        }

        return availableTimeSlots;
    }

    private void addAvailableSlots(List<TimeSlot> availableTimeSlots, int start, int end, String day, int shortActivity, int mediumActivity, int longActivity, int bufferTime) {
        while (start + longActivity <= end) {
            availableTimeSlots.add(new TimeSlot(start, start + longActivity, day));
            start += longActivity + bufferTime;
        }

        while (start + mediumActivity <= end) {
            availableTimeSlots.add(new TimeSlot(start, start + mediumActivity, day));
            start += mediumActivity + bufferTime;
        }

        while (start + shortActivity <= end) {
            availableTimeSlots.add(new TimeSlot(start, start + shortActivity, day));
            start += shortActivity + bufferTime;
        }

        if (end - start >= shortActivity + bufferTime) {
            availableTimeSlots.add(new TimeSlot(start, end, day));
        }
    }

    private void showAddActivityPopup(boolean isInEditMode, boolean isInNextWeekMode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View popupView = getLayoutInflater().inflate(R.layout.add_activity_popup, null);
        builder.setView(popupView);

        EditText editTextActivityName = popupView.findViewById(R.id.ActivityName);
        Spinner dayOfWeekSpinner = popupView.findViewById(R.id.dayOfWeekSpinner);
        EditText editTextStartTime = popupView.findViewById(R.id.StartTime);
        EditText editTextEndTime = popupView.findViewById(R.id.EndTime);

        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, daysOfWeek);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayOfWeekSpinner.setAdapter(adapter);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String activityName = editTextActivityName.getText().toString().trim();
                String selectedDay = dayOfWeekSpinner.getSelectedItem().toString();
                String startTimeInput = editTextStartTime.getText().toString().trim();
                String endTimeInput = editTextEndTime.getText().toString().trim();

                String durationType;
                boolean isWeekend = false;
                int timeOfDay;

                if (!activityName.isEmpty() && TimeConverters.isValidTimeFormat(startTimeInput) && TimeConverters.isValidTimeFormat(endTimeInput)) {
                    String startTimeHHMM = TimeConverters.convertTimeToHHMM(startTimeInput);
                    String endTimeHHMM = TimeConverters.convertTimeToHHMM(endTimeInput);
                    String startTimeFormatted = TimeConverters.convertTimeStringToFormat(startTimeInput);
                    String endTimeFormatted = TimeConverters.convertTimeStringToFormat(endTimeInput);

                    addActivityToDatabase(activityName, selectedDay, startTimeFormatted, endTimeFormatted, isInEditMode, isInNextWeekMode);

                    int duration = TimeConverters.convertTimeToMinutes(startTimeHHMM) - TimeConverters.convertTimeToMinutes(endTimeHHMM);
                    if (duration >= 240) {
                        durationType = "Long";
                    } else if (duration >= 120) {
                        durationType = "Medium";
                    } else {
                        durationType = "Short";
                    }
                    if (selectedDay.equals("Saturday") || selectedDay.equals("Sunday")) {
                        isWeekend = true;
                    }
                    if (TimeConverters.convertTimeToMinutes(startTimeHHMM) <= 720) {
                        timeOfDay = 0;
                    } else if (TimeConverters.convertTimeToMinutes(startTimeHHMM) <= 1020) {
                        timeOfDay = 1;
                    } else {
                        timeOfDay = 2;
                    }
                    String category = "Undefined";
                    MongoActivity mongoActivity = new MongoActivity(activityName, category, durationType, isWeekend, timeOfDay);
                    WeeklyMongoFunctions.sendCreatedActivityToMongoDB(mongoActivity);

                } else {
                    Toast.makeText(GeneratorActivity.this, "Please fill all fields with valid time format (HH:MM)", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addToDayLayout(ActivityInfo activity, int type) {
        LinearLayout dayLayout = findDayLayout(activity.getDay());
        if (dayLayout != null) {
            Button button = createActivityButton(activity);
            button.setTag(activity);
            if (type == 0) {
                button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#b30799")));
            } else if (type == 1) {
                button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#23a3de")));
            }
            dayLayout.addView(button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showEditActivityPopup(activity);
                }
            });
        }
    }

    private LinearLayout findDayLayout(String day) {
        int layoutId = getResources().getIdentifier(day.toLowerCase() + "View", "id", getPackageName());
        return (layoutId != 0) ? findViewById(layoutId) : null;
    }

    private Button createActivityButton(ActivityInfo activity) {
        Button button = new Button(this);
        button.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        setButtonText(button, activity);
        return button;
    }

    private void showEditActivityPopup(final ActivityInfo activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View editView = getLayoutInflater().inflate(R.layout.edit_activity_popup, null);
        builder.setView(editView);

        EditText editTextActivityName = editView.findViewById(R.id.EditActivityName);
        EditText editTextStartTime = editView.findViewById(R.id.EditStartTime);
        EditText editTextEndTime = editView.findViewById(R.id.EditEndTime);

        String formattedStart = (TimeConverters.convertFormattedToEntryTimeString(activity.getStart()));
        String formattedEnd = (TimeConverters.convertFormattedToEntryTimeString(activity.getEnd()));

        editTextActivityName.setText(activity.getName());
        editTextStartTime.setText(formattedStart);
        editTextEndTime.setText(formattedEnd);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String updatedName = editTextActivityName.getText().toString().trim();
                String updatedStartTime = editTextStartTime.getText().toString().trim();
                String updatedEndTime = editTextEndTime.getText().toString().trim();

                if (!updatedName.isEmpty() && TimeConverters.isValidTimeFormat(updatedStartTime) && TimeConverters.isValidTimeFormat(updatedEndTime)) {
                    String startTimeHHMM = TimeConverters.convertTimeToHHMM(updatedStartTime);
                    String endTimeHHMM = TimeConverters.convertTimeToHHMM(updatedEndTime);

                    activity.setName(updatedName);
                    activity.setStart(startTimeHHMM);
                    activity.setEnd(endTimeHHMM);

                    activityDatabaseHelper.updateActivity(activity);

                    Button buttonToUpdate = findActivityButton(activity.getId());
                    if (buttonToUpdate != null) {
                        setButtonText(buttonToUpdate, activity);
                    }

                    Toast.makeText(GeneratorActivity.this, "Activity updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GeneratorActivity.this, "Please enter valid times in HH:MM format.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteActivityFromDatabase(activity);
            }
        });

        builder.setNeutralButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setButtonText(Button button, ActivityInfo activity) {
        if (button != null) {
            button.setText(String.format("%s\n%s - %s", activity.getName(), activity.getStart(), activity.getEnd()));
        }
    }

    private void deleteActivityFromDatabase(ActivityInfo activity) {
        activityDatabaseHelper.deleteActivity(activity.getId());
        removeActivityButton(activity);
        Toast.makeText(this, "Activity deleted successfully", Toast.LENGTH_SHORT).show();
    }

    private void removeActivityButton(ActivityInfo activity) {
        LinearLayout dayLayout = findDayLayout(activity.getDay());
        if (dayLayout != null) {
            for (int i = 0; i < dayLayout.getChildCount(); i++) {
                View child = dayLayout.getChildAt(i);
                if (child instanceof Button) {
                    ActivityInfo tag = (ActivityInfo) child.getTag();
                    if (tag != null && tag.getId() == activity.getId()) {
                        dayLayout.removeView(child);
                        break;
                    }
                }
            }
        }
    }

    private Button findActivityButton(long id) {
        LinearLayout[] dayLayouts = {findViewById(R.id.mondayView), findViewById(R.id.tuesdayView), findViewById(R.id.wednesdayView), findViewById(R.id.thursdayView), findViewById(R.id.fridayView), findViewById(R.id.saturdayView), findViewById(R.id.sundayView)};
        for (LinearLayout layout : dayLayouts) {
            for (int j = 0; j < layout.getChildCount(); j++) {
                View view = layout.getChildAt(j);
                if (view instanceof Button) {
                    ActivityInfo tag = (ActivityInfo) view.getTag();
                    if (tag != null && tag.getId() == id) {
                        return (Button) view;
                    }
                }
            }
        }
        return null;
    }
}