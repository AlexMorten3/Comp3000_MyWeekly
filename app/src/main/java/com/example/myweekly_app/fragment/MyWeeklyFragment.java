package com.example.myweekly_app.fragment;

import static com.example.myweekly_app.helper.TimeConverters.convertFormattedToMinutes;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.myweekly_app.R;
import com.example.myweekly_app.model.ActivityInfo;
import com.example.myweekly_app.helper.ActivityDatabaseHelper;
import com.example.myweekly_app.helper.StaticActivityDatabaseHelper;
import com.example.myweekly_app.status.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyWeeklyFragment extends Fragment {

    private static final String KEY_IS_COLOR_CHANGER = "isColorChanger";
    private boolean isButtonColorStatic;
    private LinearLayout monView;
    private LinearLayout tueView;
    private LinearLayout wedView;
    private LinearLayout thuView;
    private LinearLayout friView;
    private LinearLayout satView;
    private LinearLayout sunView;

    private ActivityDatabaseHelper activityDatabaseHelper;
    private StaticActivityDatabaseHelper staticActivityDatabaseHelper;

    private Button editButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_weekly, container, false);

        activityDatabaseHelper = new ActivityDatabaseHelper(requireContext());
        staticActivityDatabaseHelper = new StaticActivityDatabaseHelper(requireContext());

        isButtonColorStatic = loadButtonColorStaticSetting();

        monView = rootView.findViewById(R.id.monView);
        tueView = rootView.findViewById(R.id.tueView);
        wedView = rootView.findViewById(R.id.wedView);
        thuView = rootView.findViewById(R.id.thuView);
        friView = rootView.findViewById(R.id.friView);
        satView = rootView.findViewById(R.id.satView);
        sunView = rootView.findViewById(R.id.sunView);

        setButtonColorBehavior(isButtonColorStatic);
        displayActivitiesInOrder();

        return rootView;
    }

    private void displayActivitiesInOrder() {
        List<ActivityInfo> activities = activityDatabaseHelper.getAllActivities();

        Map<String, List<ActivityInfo>> activitiesByDay = new HashMap<>();

        for (ActivityInfo activity : activities) {
            String day = activity.getDay();
            if (!activitiesByDay.containsKey(day)) {
                activitiesByDay.put(day, new ArrayList<>());
            }
            activitiesByDay.get(day).add(activity);
        }

        for (Map.Entry<String, List<ActivityInfo>> entry : activitiesByDay.entrySet()) {
            String day = entry.getKey();
            List<ActivityInfo> dayActivities = entry.getValue();

            Collections.sort(dayActivities, new Comparator<ActivityInfo>() {
                @Override
                public int compare(ActivityInfo activity1, ActivityInfo activity2) {
                    // Convert start times to minutes since midnight
                    int minutes1 = convertFormattedToMinutes(activity1.getStart());
                    int minutes2 = convertFormattedToMinutes(activity2.getStart());

                    // Compare activities based on start times in minutes
                    return Integer.compare(minutes1, minutes2);
                }
            });

            for (ActivityInfo activity : dayActivities) {
                addButtonToLayout(getDayLayout(day), activity);
                Log.d("Getting All Activities", "Activity name: " + activity.getName()
                        + " Activity start: " + activity.getStart()
                        + " Activity end: " + activity.getEnd());
            }
        }
    }


    private void addButtonToLayout(LinearLayout layout, ActivityInfo activity) {
        if (layout != null) {
            Button button = createButtonForActivity(activity);
            layout.addView(button);
        } else {
            Toast.makeText(requireContext(), "No layout found for day: " + activity.getDay(), Toast.LENGTH_SHORT).show();
        }
    }

    private Button createButtonForActivity(ActivityInfo activity) {
        Button button = new Button(requireContext());
        button.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        Log.d("Button text setter", "Activity name: " + activity.getName()
                +" Activity start: " + activity.getStart() + " Activity end: " + activity.getEnd());

        String buttonText = String.format("%s\n%s - %s", activity.getName(), activity.getStart(), activity.getEnd());

        button.setText(buttonText);
        button.setGravity(Gravity.CENTER);
        button.setBackgroundTintList(ColorStateList.valueOf(getColorForState(activity.getState())));

        button.setOnClickListener(v -> {
            int state = activityDatabaseHelper.getCurrentActivityState((int) activity.getId());
            cycleButtonColor(button);
            int nextActivityState = getNextState(state);
            activityDatabaseHelper.updateActivityState((int) activity.getId(), nextActivityState);
        });

        button.setTag(activity);
        return button;
    }

    private void cycleButtonColor(Button button) {
        ColorStateList colorStateList = button.getBackgroundTintList();
        if (colorStateList != null) {
            int currentColor = colorStateList.getDefaultColor();
            int nextColor;
            if (SharedPreferenceManager.isColorChanger()) {
                if (currentColor == Color.parseColor("#e33232")) {
                    nextColor = Color.parseColor("#dec431");
                } else if (currentColor == Color.parseColor("#dec431")) {
                    nextColor = Color.parseColor("#41d94b");
                } else if (currentColor == Color.parseColor("#41d94b")) {
                    nextColor = Color.parseColor("#e33232");
                } else {
                    nextColor = Color.parseColor("#e33232");
                }

                button.setBackgroundTintList(ColorStateList.valueOf(nextColor));
            } else {
                if (currentColor == Color.parseColor("#e6993c")) {
                    nextColor = Color.parseColor("#e6993c");
                } else if (currentColor == Color.parseColor("#e6993c")) {
                    nextColor = Color.parseColor("#e6993c");
                } else if (currentColor == Color.parseColor("#e6993c")) {
                    nextColor = Color.parseColor("#e6993c");
                } else {
                    nextColor = Color.parseColor("#e6993c");
                }

                button.setBackgroundTintList(ColorStateList.valueOf(nextColor));
            }
        }
    }


    private int getNextState(int currentState) {
        if (SharedPreferenceManager.isColorChanger()) {
            return (currentState % 3) + 1;
        } else {
            return (currentState);
        }
    }

    private LinearLayout getDayLayout(String day) {
        switch (day) {
            case "Monday":
                return monView;
            case "Tuesday":
                return tueView;
            case "Wednesday":
                return wedView;
            case "Thursday":
                return thuView;
            case "Friday":
                return friView;
            case "Saturday":
                return satView;
            case "Sunday":
                return sunView;
            default:
                return null;
        }
    }

    private int getColorForState(int state) {
        if (SharedPreferenceManager.isColorChanger()) {
            switch (state) {
                case 1:
                    return Color.parseColor("#e33232");
                case 2:
                    return Color.parseColor("#dec431");
                case 3:
                    return Color.parseColor("#41d94b");
                default:
                    return Color.parseColor("#e33232");
            }
        } else {
            switch (state) {
                case 1:
                    return Color.parseColor("#e6993c");
                case 2:
                    return Color.parseColor("#e6993c");
                case 3:
                    return Color.parseColor("#e6993c");
                default:
                    return Color.parseColor("#e6993c");
            }
        }
    }

    private boolean loadButtonColorStaticSetting() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_IS_COLOR_CHANGER, false);
    }


    private void saveButtonColorStaticSetting(boolean isStatic) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_COLOR_CHANGER, isStatic);
        editor.apply();
    }

    private void setButtonColorBehavior(boolean isStatic) {
        if (isStatic) {
            setAllButtonsColor(Color.BLUE);
        } else {
            setDynamicButtonColors();
        }
    }

    private void setAllButtonsColor(int color) {
        List<LinearLayout> dayLayouts = Arrays.asList(monView, tueView, wedView, thuView, friView, satView, sunView);

        for (LinearLayout layout : dayLayouts) {
            if (layout != null) {
                for (int i = 0; i < layout.getChildCount(); i++) {
                    View child = layout.getChildAt(i);
                    if (child instanceof Button) {
                        Button button = (Button) child;
                        button.setBackgroundTintList(ColorStateList.valueOf(color));
                    }
                }
            }
        }
    }

    private void setDynamicButtonColors() {
        List<LinearLayout> dayLayouts = Arrays.asList(monView, tueView, wedView, thuView, friView, satView, sunView);

        for (LinearLayout layout : dayLayouts) {
            if (layout != null) {
                for (int i = 0; i < layout.getChildCount(); i++) {
                    View child = layout.getChildAt(i);
                    if (child instanceof Button) {
                        Button button = (Button) child;
                        if (button.getTag() instanceof ActivityInfo) {
                            ActivityInfo activity = (ActivityInfo) button.getTag();
                            int color = getColorForState(activity.getState());
                            button.setBackgroundTintList(ColorStateList.valueOf(color));
                        }
                    }
                }
            }
        }
    }
}