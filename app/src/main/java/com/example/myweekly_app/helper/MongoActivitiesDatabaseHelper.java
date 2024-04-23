package com.example.myweekly_app.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.myweekly_app.model.ActivityInfo;
import com.example.myweekly_app.model.MongoActivity;
import com.example.myweekly_app.model.StaticActivityInfo;

import java.util.ArrayList;
import java.util.List;

public class MongoActivitiesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MongoDefaultActivities.db";
    private static final int DATABASE_VERSION = 3;


    private static final String TABLE_MONGO_ACTIVITIES = "mongo_activities";
    private static final String COLUMN_MONGO_ID = "_id";
    private static final String COLUMN_MONGO_ACTIVITY_NAME = "name";
    private static final String COLUMN_MONGO_ACTIVITY_CATEGORY = "category";
    private static final String COLUMN_MONGO_ACTIVITY_DURATION = "duration";
    private static final String COLUMN_MONGO_ACTIVITY_WEEKEND = "isWeekend";
    private static final String COLUMN_MONGO_ACTIVITY_TIME = "timeOfDay";

    public MongoActivitiesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STATIC_ACTIVITIES_TABLE = "CREATE TABLE " + TABLE_MONGO_ACTIVITIES + " ("
                + COLUMN_MONGO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_MONGO_ACTIVITY_NAME + " TEXT,"
                + COLUMN_MONGO_ACTIVITY_CATEGORY + " TEXT,"
                + COLUMN_MONGO_ACTIVITY_DURATION + " TEXT,"
                + COLUMN_MONGO_ACTIVITY_WEEKEND + " BOOLEAN,"
                + COLUMN_MONGO_ACTIVITY_TIME + " INTEGER)";
        db.execSQL(CREATE_STATIC_ACTIVITIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MONGO_ACTIVITIES);
        onCreate(db);
    }

    public void deleteAllActivities() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MONGO_ACTIVITIES, null, null);
        db.close();
    }

    public long addMongoGeneratorActivity(MongoActivity activityInfo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MONGO_ACTIVITY_NAME, activityInfo.getName());
        values.put(COLUMN_MONGO_ACTIVITY_CATEGORY, activityInfo.getCategory());
        values.put(COLUMN_MONGO_ACTIVITY_DURATION, activityInfo.getDuration());
        values.put(COLUMN_MONGO_ACTIVITY_WEEKEND, activityInfo.getIsWeekend());
        values.put(COLUMN_MONGO_ACTIVITY_TIME, activityInfo.getTimeOfDay());
        long result = db.insert(TABLE_MONGO_ACTIVITIES, null, values);
        db.close();
        return result;
    }

    public List<MongoActivity> getAllMongoGeneratorActivities() {
        List<MongoActivity> mongoGeneratorActivities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MONGO_ACTIVITIES, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(COLUMN_MONGO_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_MONGO_ACTIVITY_NAME));
                String category = cursor.getString(cursor.getColumnIndex(COLUMN_MONGO_ACTIVITY_CATEGORY));
                String duration = cursor.getString(cursor.getColumnIndex(COLUMN_MONGO_ACTIVITY_DURATION));
                boolean isWeekend = cursor.getExtras().getBoolean(String.valueOf(cursor.getColumnIndex(COLUMN_MONGO_ACTIVITY_WEEKEND)));
                int timeOfDay = cursor.getInt(cursor.getColumnIndex(COLUMN_MONGO_ACTIVITY_TIME));

                MongoActivity mongoActivity = new MongoActivity(name, category, duration, isWeekend, timeOfDay);
                mongoGeneratorActivities.add(mongoActivity);
            } while (cursor.moveToNext());

            cursor.close();
        }
        db.close();
        return mongoGeneratorActivities;
    }

    public List<MongoActivity> getMongoActivitiesByFilter(String searchDuration, boolean searchIsWeekend, int searchTimeOfDay) {
        List<MongoActivity> foundMongoGeneratorActivities = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + TABLE_MONGO_ACTIVITIES
                    + " WHERE " + COLUMN_MONGO_ACTIVITY_DURATION + " = ?"
                    + " AND " + COLUMN_MONGO_ACTIVITY_WEEKEND + " = ?"
                    + " AND " + COLUMN_MONGO_ACTIVITY_TIME + " = ?";

            cursor = db.rawQuery(query, new String[]{searchDuration, searchIsWeekend ? "1" : "0", String.valueOf(searchTimeOfDay)});

            while (cursor != null && cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(COLUMN_MONGO_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_MONGO_ACTIVITY_NAME));
                String category = cursor.getString(cursor.getColumnIndex(COLUMN_MONGO_ACTIVITY_CATEGORY));
                String duration = cursor.getString(cursor.getColumnIndex(COLUMN_MONGO_ACTIVITY_DURATION));
                boolean isWeekend = cursor.getExtras().getBoolean(String.valueOf((cursor.getColumnIndex(COLUMN_MONGO_ACTIVITY_WEEKEND))));
                int timeOfDay = cursor.getInt(cursor.getColumnIndex(COLUMN_MONGO_ACTIVITY_TIME));

                MongoActivity activity = new MongoActivity(name, category, duration, isWeekend, timeOfDay);

                Log.d("Filtered Mongo Items", "Found match: " + activity);
                foundMongoGeneratorActivities.add(activity);
            }

            Log.d("Filtered Mongo Items", "Matching items: " + foundMongoGeneratorActivities);
        } catch (Exception e) {
            Log.e("Filtered Mongo Items", "Error retrieving activities: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return foundMongoGeneratorActivities;
    }
}