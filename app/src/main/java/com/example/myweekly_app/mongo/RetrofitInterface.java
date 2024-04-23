package com.example.myweekly_app.mongo;

import com.example.myweekly_app.model.MongoActivity;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @POST("weekly/insert")
    Call<Void> executeInsertWeekly(@Body HashMap<String, Object> weeklyPlan);

    @GET("activities/getAll")
    Call<List<MongoActivity>> getAllMongoItems();

    @POST("activities/insert")
    Call<Void> executeInsertDefaultActivity(@Body MongoActivity mongoActivity);

}
