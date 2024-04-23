package com.example.myweekly_app.status;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {

    private static final String PREF_NAME = "SharedPreferences";
    private static final String KEY_IS_COLOR_CHANGER = "isColorChanger";
    private static final String KEY_IS_ACTIVE = "isActive";
    private static final String KEY_IS_CREATED = "isCreated";
    private static final String KEY_IS_GENERATED = "isGenerated";
    private static final String KEY_ENABLE_NOTIFICATIONS = "isNotifications";
    private static final String KEY_INCLUDE_STATIC_ACTIVITIES = "includeStatic";
    private static final String KEY_INCLUDE_ACTIVITY_RECOMMENDATIONS = "includeRecommendations";
    private static final String KEY_IS_LOADED = "isLoaded";

    private static SharedPreferences sharedPreferences;

    public SharedPreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isLoaded() {
        return sharedPreferences.getBoolean(KEY_IS_LOADED, false);
    }

    public static void setIsLoaded(boolean isLoaded) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOADED, isLoaded);
        editor.apply();
    }

    public static boolean isColorChanger() {
        return sharedPreferences.getBoolean(KEY_IS_COLOR_CHANGER, false);
    }

    public static void setIsColorChanger(boolean isColorChanger) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_COLOR_CHANGER, isColorChanger);
        editor.apply();
    }


    public void saveWeeklyStatus(boolean isActive, boolean isCreated) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_ACTIVE, isActive);
        editor.putBoolean(KEY_IS_CREATED, isCreated);
        editor.apply();
    }

    public void saveSettings(boolean inNotifications, boolean includeStatic, boolean includeRecommendations) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_ENABLE_NOTIFICATIONS, inNotifications);
        editor.putBoolean(KEY_INCLUDE_STATIC_ACTIVITIES, includeStatic);
        editor.putBoolean(KEY_INCLUDE_ACTIVITY_RECOMMENDATIONS, includeRecommendations);
        editor.apply();
    }

    public static boolean isActiveWeekly() {
        return sharedPreferences.getBoolean(KEY_IS_ACTIVE, false);
    }

    public boolean isCreatedWeekly() {
        return sharedPreferences.getBoolean(KEY_IS_CREATED, false);
    }

    public boolean isNotificationsEnabled() {
        return sharedPreferences.getBoolean(KEY_ENABLE_NOTIFICATIONS, false);
    }

    public static boolean isStaticActivitiesIncluded() {
        return sharedPreferences.getBoolean(KEY_INCLUDE_STATIC_ACTIVITIES, false);
    }

    public static boolean isActivityRecommendationsIncluded() {
        return sharedPreferences.getBoolean(KEY_INCLUDE_ACTIVITY_RECOMMENDATIONS, false);
    }

    public static boolean isGenerated() {
        return sharedPreferences.getBoolean(KEY_IS_GENERATED, false);
    }

    public static void setIsGenerated(boolean isGenerated) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_GENERATED, isGenerated);
        editor.apply();
    }


    public static void setIsActive(boolean isActive) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_ACTIVE, isActive);
        editor.apply();
    }

    public static void setIsCreated(boolean isCreated) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_CREATED, isCreated);
        editor.apply();
    }

    public static boolean isCreated() {
        return sharedPreferences.getBoolean(KEY_IS_CREATED, false);
    }

    public boolean isGenerationEnabled() {
        return sharedPreferences.getBoolean(KEY_INCLUDE_ACTIVITY_RECOMMENDATIONS, false);
    }
}