package com.example.myweekly_app.mongo;

import android.content.Context;
import android.util.Log;

import com.example.myweekly_app.helper.MongoActivitiesDatabaseHelper;
import com.example.myweekly_app.helper.ActivityDatabaseHelper;
import com.example.myweekly_app.model.MongoActivity;
import com.example.myweekly_app.model.UserInfo;
import com.example.myweekly_app.model.ActivityInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.List;

public class WeeklyMongoFunctions {

    private MongoActivitiesDatabaseHelper mongoActivitiesDatabaseHelper;

    public WeeklyMongoFunctions(Context context) {
        this.mongoActivitiesDatabaseHelper = new MongoActivitiesDatabaseHelper(context);

    }

    public static void main(Context context) {
        String userEmail = UserInfo.getEmail();
        ActivityDatabaseHelper databaseHelper = new ActivityDatabaseHelper(context);
        List<ActivityInfo> activities = databaseHelper.getAllActivities();
        HashMap<String, Object> weeklyPlan = constructWeeklyPlan(userEmail, activities);

        sendWeeklyPlanToMongoDB(weeklyPlan);
    }

    public void retrieveActivitiesFromMongoDB() {
        RetrofitInterface retrofitInterface = MyApiClient.getRetrofitInterface();

        Call<List<MongoActivity>> call = retrofitInterface.getAllMongoItems();
        call.enqueue(new Callback<List<MongoActivity>>() {
            @Override
            public void onResponse(Call<List<MongoActivity>> call, Response<List<MongoActivity>> response) {
                if (response.isSuccessful()) {
                    List<MongoActivity> mongoActivities = response.body();
                    mongoActivitiesDatabaseHelper.deleteAllActivities();
                    processMongoActivities(mongoActivities);
                } else {
                    System.out.println("Failed to retrieve activities. Server returned error.");
                }
            }

            @Override
            public void onFailure(Call<List<MongoActivity>> call, Throwable t) {
                System.out.println("Failed to retrieve activities. Network error: " + t.getMessage());
            }
        });
    }

    private void processMongoActivities(List<MongoActivity> mongoActivities) {
        for (MongoActivity activity : mongoActivities) {
            mongoActivitiesDatabaseHelper.addMongoGeneratorActivity(activity);
            Log.d("Pull from Mongo", "Pulled activity: " + activity.getName());
        }
    }

    private static HashMap<String, Object> constructWeeklyPlan(String userEmail, List<ActivityInfo> activities) {
        HashMap<String, Object> weeklyPlan = new HashMap<>();
        weeklyPlan.put("user", userEmail);
        weeklyPlan.put("activities", activities);
        return weeklyPlan;
    }

    private static void sendWeeklyPlanToMongoDB(HashMap<String, Object> weeklyPlan) {
        RetrofitInterface retrofitInterface = MyApiClient.getRetrofitInterface();

        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("user", weeklyPlan.get("user"));
        requestBody.put("activities", weeklyPlan.get("activities"));

        Call<Void> call = retrofitInterface.executeInsertWeekly(requestBody);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("Weekly plan data sent to MongoDB successfully!");
                } else {
                    System.out.println("Failed to send weekly plan data to MongoDB.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("Error sending weekly plan data to MongoDB: " + t.getMessage());
            }
        });
    }
    public static void sendCreatedActivityToMongoDB(MongoActivity newDefaultActivity) {
        RetrofitInterface retrofitInterface = MyApiClient.getRetrofitInterface();

        Call<Void> call = retrofitInterface.executeInsertDefaultActivity(newDefaultActivity);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("Activity data sent to MongoDB successfully!");
                } else {
                    System.out.println("Failed to send activity data to MongoDB.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("Error sending activity data to MongoDB: " + t.getMessage());
            }
        });
    }

}

