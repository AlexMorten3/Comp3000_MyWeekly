package com.example.myweekly_app.mongo;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyApiClient {

    private static final String BASE_URL = "https://eu-west-1.aws.data.mongodb-api.com/app/application-0-csvmn/endpoint/";
    private static final String APP_ID = "application-0-csvmn";

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static RetrofitInterface getRetrofitInterface() {
        return retrofit.create(RetrofitInterface.class);
    }


}
