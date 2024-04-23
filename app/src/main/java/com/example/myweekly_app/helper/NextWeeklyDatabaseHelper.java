package com.example.myweekly_app.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.myweekly_app.model.ActivityInfo;

import java.util.ArrayList;
import java.util.List;

public class NextWeeklyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "activities.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_ACTIVITIES = "activities";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_ACTIVITY_NAME = "name";
    private static final String COLUMN_ACTIVITY_STATE = "state";
    private static final String COLUMN_ACTIVITY_DAY = "day";
    private static final String COLUMN_ACTIVITY_START = "startTime";
    private static final String COLUMN_ACTIVITY_END = "endTime";


    public NextWeeklyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ACTIVITIES_TABLE = "CREATE TABLE " + TABLE_ACTIVITIES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ACTIVITY_NAME + " TEXT,"
                + COLUMN_ACTIVITY_STATE + " INTEGER,"
                + COLUMN_ACTIVITY_DAY + " TEXT,"
                + COLUMN_ACTIVITY_START + " TEXT,"
                + COLUMN_ACTIVITY_END + " TEXT" + ")";
        db.execSQL(CREATE_ACTIVITIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITIES);
        onCreate(db);
    }

    public long addActivity(ActivityInfo activityInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACTIVITY_NAME, activityInfo.getName());
        values.put(COLUMN_ACTIVITY_STATE, activityInfo.getState());
        values.put(COLUMN_ACTIVITY_DAY, activityInfo.getDay());
        values.put(COLUMN_ACTIVITY_START, activityInfo.getStart());
        values.put(COLUMN_ACTIVITY_END, activityInfo.getEnd());

        long result = db.insert(TABLE_ACTIVITIES, null, values);
        db.close();
        return result;
    }

    public static void deleteAllActivities(Context context) {
        ActivityDatabaseHelper dbHelper = new ActivityDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_ACTIVITIES, null, null);
        db.close();
    }

    public void deleteActivity(long activityId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACTIVITIES, COLUMN_ID + " = ?", new String[]{String.valueOf(activityId)});
        db.close();
    }

    public void updateActivity(ActivityInfo activity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACTIVITY_NAME, activity.getName());
        values.put(COLUMN_ACTIVITY_STATE, activity.getState());
        values.put(COLUMN_ACTIVITY_DAY, activity.getDay());
        values.put(COLUMN_ACTIVITY_START, activity.getStart());
        values.put(COLUMN_ACTIVITY_END, activity.getEnd());

        db.update(TABLE_ACTIVITIES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(activity.getId())});
        db.close();
    }

    public List<ActivityInfo> getAllActivities() {
        List<ActivityInfo> activities = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_ACTIVITIES, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
                String activityName = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_NAME));
                int state = cursor.getInt(cursor.getColumnIndex(COLUMN_ACTIVITY_STATE));
                String day = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_DAY));
                String startTime = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_START));
                String endTime = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_END));

                ActivityInfo activity = new ActivityInfo(activityName, state, day, startTime, endTime);
                activity.setId(id);
                activities.add(activity);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return activities;
    }

    public int updateActivityState(int activityId, int newState) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACTIVITY_STATE, newState);
        int rowsAffected = db.update(TABLE_ACTIVITIES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(activityId)});
        db.close();
        return rowsAffected;
    }

    public int getCurrentActivityState(int activityId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int currentState = -1; // Default state if activity is not found

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_ACTIVITIES,
                    new String[]{COLUMN_ACTIVITY_STATE},
                    COLUMN_ID + " = ?",
                    new String[]{String.valueOf(activityId)},
                    null,
                    null,
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                currentState = cursor.getInt(cursor.getColumnIndex(COLUMN_ACTIVITY_STATE));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        db.close();
        return currentState;
    }
}
