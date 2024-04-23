package com.example.myweekly_app.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.myweekly_app.model.ActivityInfo;
import com.example.myweekly_app.model.StaticActivityInfo;

import java.util.ArrayList;
import java.util.List;

public class StaticActivityDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "static_activities.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_STATIC_ACTIVITIES = "static_activities";
    private static final String COLUMN_STATIC_ID = "_id";
    private static final String COLUMN_ACTIVITY_NAME = "static_name";
    private static final String COLUMN_ACTIVITY_STATE = "static_state";
    private static final String COLUMN_ACTIVITY_DAY = "static_day";
    private static final String COLUMN_ACTIVITY_START = "static_start";
    private static final String COLUMN_ACTIVITY_END = "static_end";

    public StaticActivityDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STATIC_ACTIVITIES_TABLE = "CREATE TABLE " + TABLE_STATIC_ACTIVITIES + " ("
                + COLUMN_STATIC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ACTIVITY_NAME + " TEXT,"
                + COLUMN_ACTIVITY_STATE + " INTEGER,"
                + COLUMN_ACTIVITY_DAY + " TEXT,"
                + COLUMN_ACTIVITY_START + " TEXT,"
                + COLUMN_ACTIVITY_END + " TEXT)";
        db.execSQL(CREATE_STATIC_ACTIVITIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATIC_ACTIVITIES);
        onCreate(db);
    }

    public long addStaticActivity(StaticActivityInfo activityInfo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACTIVITY_NAME, activityInfo.getName());
        values.put(COLUMN_ACTIVITY_STATE, activityInfo.getState());
        values.put(COLUMN_ACTIVITY_DAY, activityInfo.getDay());
        values.put(COLUMN_ACTIVITY_START, activityInfo.getStart());
        values.put(COLUMN_ACTIVITY_END, activityInfo.getEnd());
        long result = db.insert(TABLE_STATIC_ACTIVITIES, null, values);
        db.close();
        return result;
    }


    public List<ActivityInfo> getAllStaticActivities() {
        List<ActivityInfo> staticActivities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_STATIC_ACTIVITIES, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(COLUMN_STATIC_ID));
                String activityName = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_NAME));
                int state = cursor.getInt(cursor.getColumnIndex(COLUMN_ACTIVITY_STATE));
                String day = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_DAY));
                String startTime = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_START));
                String endTime = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_END));

                String convertedStartTime = TimeConverters.convertTimeStringToFormat(startTime);
                String convertedEndTime = TimeConverters.convertTimeStringToFormat(endTime);

                ActivityInfo staticActivity = new ActivityInfo(activityName, state, day, convertedStartTime, convertedEndTime);
                staticActivities.add(staticActivity);
            } while (cursor.moveToNext());

            cursor.close();
        }
        db.close();
        return staticActivities;
    }

    public int updateActivityState(String activityName, String dayOfWeek, int newState) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACTIVITY_STATE, newState);

        String whereClause = COLUMN_ACTIVITY_NAME + " = ? AND " + COLUMN_ACTIVITY_DAY + " = ?";
        String[] whereArgs = { activityName, dayOfWeek };

        int rowsAffected = db.update(TABLE_STATIC_ACTIVITIES, values, whereClause, whereArgs);
        db.close();
        return rowsAffected;
    }

    public List<String> getSelectedDaysForStaticActivity(String activityName) {
        List<String> selectedDays = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT DISTINCT " + COLUMN_ACTIVITY_DAY + " FROM " + TABLE_STATIC_ACTIVITIES +
                " WHERE " + COLUMN_ACTIVITY_NAME + " = ?";
        String[] selectionArgs = { activityName };

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String dayOfWeek = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_DAY));
                selectedDays.add(dayOfWeek);
            }
            cursor.close();
        }
        db.close();
        return selectedDays;
    }

    public void deleteStaticActivitiesByName(String activityName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STATIC_ACTIVITIES, COLUMN_ACTIVITY_NAME + " = ?", new String[]{activityName});
        db.close();
    }

    public String getSelectedDaysStringForActivity(String activityName) {
        List<String> days = getSelectedDaysForStaticActivity(activityName);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < days.size(); i++) {
            sb.append(days.get(i));
            if (i < days.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public List<String> queryDatabaseForSelectedDays(String activityName) {
        List<String> selectedDays = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_STATIC_ACTIVITIES +
                " WHERE " + COLUMN_ACTIVITY_NAME + " = ?";
        String[] selectionArgs = { activityName };

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String dayOfWeek = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_DAY));

                if (!selectedDays.contains(dayOfWeek)) {
                    selectedDays.add(dayOfWeek);
                }
            }
            cursor.close();
        }

        db.close();
        return selectedDays;
    }


    public List<String> getAllStaticActivityNames() {
        List<String> activityNames = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT DISTINCT " + COLUMN_ACTIVITY_NAME + " FROM " + TABLE_STATIC_ACTIVITIES, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String activityName = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_NAME));
                activityNames.add(activityName);
            }
            cursor.close();
        }
        db.close();
        return activityNames;
    }

    public int deleteAllStaticActivities() {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = 0;

        try {
            deletedRows = db.delete(TABLE_STATIC_ACTIVITIES, null, null);
        } catch (Exception e) {
        } finally {
            db.close();
        }

        return deletedRows;
    }

    public List<StaticActivityInfo> getStaticActivitiesByName(String activityName) {
        List<StaticActivityInfo> activities = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_STATIC_ACTIVITIES + " WHERE " + COLUMN_ACTIVITY_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{activityName});

        if (cursor != null && cursor.moveToFirst()) {
            activities.clear();
            do {
                long id = cursor.getLong(cursor.getColumnIndex(COLUMN_STATIC_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_NAME));
                int state = cursor.getInt(cursor.getColumnIndex(COLUMN_ACTIVITY_STATE)); // Retrieve the state
                String dayOfWeek = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_DAY));
                String startTime = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_START));
                String endTime = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_END));

                StaticActivityInfo activity = new StaticActivityInfo(name, dayOfWeek, startTime, endTime, state);
                activity.setId(id);

                activities.add(activity);

            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        Log.d("Static List", "Returning complete list: " + activities);
        return activities;
    }
}