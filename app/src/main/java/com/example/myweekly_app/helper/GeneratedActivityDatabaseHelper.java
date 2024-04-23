package com.example.myweekly_app.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.myweekly_app.model.ActivityInfo;
import com.example.myweekly_app.model.GeneratedActivityInfo;

import java.util.ArrayList;
import java.util.List;

public class GeneratedActivityDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "generated_activities.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_GENERATED_ACTIVITIES = "generated_activities";
    private static final String COLUMN_GENERATED_ID = "_id";
    private static final String COLUMN_GENERATED_NAME = "generated_name";
    private static final String COLUMN_GENERATED_STATE = "generated_state";
    private static final String COLUMN_GENERATED_DAY = "generated_day";
    private static final String COLUMN_GENERATED_START = "generated_start";
    private static final String COLUMN_GENERATED_END = "generated_end";

    public GeneratedActivityDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_GENERATED_ACTIVITIES_TABLE = "CREATE TABLE " + TABLE_GENERATED_ACTIVITIES + " ("
                + COLUMN_GENERATED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_GENERATED_NAME + " TEXT,"
                + COLUMN_GENERATED_STATE + " INTEGER,"
                + COLUMN_GENERATED_DAY + " TEXT,"
                + COLUMN_GENERATED_START + " TEXT,"
                + COLUMN_GENERATED_END + " TEXT)";
        db.execSQL(CREATE_GENERATED_ACTIVITIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENERATED_ACTIVITIES);
        onCreate(db);
    }

    public static void deleteAllActivities(Context context) {
        GeneratedActivityDatabaseHelper dbHelper = new GeneratedActivityDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_GENERATED_ACTIVITIES, null, null);
        db.close();
    }


    public long addGeneratedActivity(GeneratedActivityInfo activityInfo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GENERATED_NAME, activityInfo.getName());
        values.put(COLUMN_GENERATED_STATE, activityInfo.getState());
        values.put(COLUMN_GENERATED_DAY, activityInfo.getDay());
        values.put(COLUMN_GENERATED_START, activityInfo.getStart());
        values.put(COLUMN_GENERATED_END, activityInfo.getEnd());
        long result = db.insert(TABLE_GENERATED_ACTIVITIES, null, values);
        db.close();
        return result;
    }

    public List<ActivityInfo> getAllGeneratedActivities() {
        List<ActivityInfo> generatedActivities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_GENERATED_ACTIVITIES, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(COLUMN_GENERATED_ID));
                String activityName = cursor.getString(cursor.getColumnIndex(COLUMN_GENERATED_NAME));
                int state = cursor.getInt(cursor.getColumnIndex(COLUMN_GENERATED_STATE));
                String day = cursor.getString(cursor.getColumnIndex(COLUMN_GENERATED_DAY));
                String startTime = cursor.getString(cursor.getColumnIndex(COLUMN_GENERATED_START));
                String endTime = cursor.getString(cursor.getColumnIndex(COLUMN_GENERATED_END));

                String convertedStartTime = TimeConverters.convertTimeStringToFormat(startTime);
                String convertedEndTime = TimeConverters.convertTimeStringToFormat(endTime);

                ActivityInfo generatedActivity = new ActivityInfo(activityName, state, day, convertedStartTime, convertedEndTime);
                generatedActivities.add(generatedActivity);
            } while (cursor.moveToNext());

            cursor.close();
        }
        db.close();
        return generatedActivities;
    }

    public int updateActivityState(String activityName, String dayOfWeek, int newState) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GENERATED_STATE, newState);

        String whereClause = COLUMN_GENERATED_NAME + " = ? AND " + COLUMN_GENERATED_DAY + " = ?";
        String[] whereArgs = { activityName, dayOfWeek };

        int rowsAffected = db.update(TABLE_GENERATED_ACTIVITIES, values, whereClause, whereArgs);
        db.close();
        return rowsAffected;
    }

    public void deleteGeneratedActivityByName(String activityName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GENERATED_ACTIVITIES, COLUMN_GENERATED_NAME + " = ?", new String[]{activityName});
        db.close();
    }

    public List<String> getAllGeneratedActivityNames() {
        List<String> activityNames = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT DISTINCT " + COLUMN_GENERATED_NAME + " FROM " + TABLE_GENERATED_ACTIVITIES, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String activityName = cursor.getString(cursor.getColumnIndex(COLUMN_GENERATED_NAME));
                activityNames.add(activityName);
            }
            cursor.close();
        }
        db.close();
        return activityNames;
    }

    public int deleteAllGeneratedActivities() {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = 0;

        try {
            deletedRows = db.delete(TABLE_GENERATED_ACTIVITIES, null, null);
        } catch (Exception e) {
        } finally {
            db.close();
        }

        return deletedRows;
    }

    public List<GeneratedActivityInfo> getGeneratedActivitiesByName(String activityName) {
        List<GeneratedActivityInfo> activities = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_GENERATED_ACTIVITIES + " WHERE " + COLUMN_GENERATED_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{activityName});

        if (cursor != null && cursor.moveToFirst()) {
            activities.clear();
            do {
                long id = cursor.getLong(cursor.getColumnIndex(COLUMN_GENERATED_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_GENERATED_NAME));
                int state = cursor.getInt(cursor.getColumnIndex(COLUMN_GENERATED_STATE)); // Retrieve the state
                String dayOfWeek = cursor.getString(cursor.getColumnIndex(COLUMN_GENERATED_DAY));
                String startTime = cursor.getString(cursor.getColumnIndex(COLUMN_GENERATED_START));
                String endTime = cursor.getString(cursor.getColumnIndex(COLUMN_GENERATED_END));

                GeneratedActivityInfo activity = new GeneratedActivityInfo(name, dayOfWeek, startTime, endTime, state);
                activity.setId(id);

                Log.d("Generated List", "Added static activity: " + activity);

                activities.add(activity);
                Log.d("Generated List", "Current list of static activities: " + activities);

            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        Log.d("Generated List", "Returning complete list: " + activities);
        return activities;
    }


}
