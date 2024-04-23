package com.example.myweekly_app.mongo;

import com.example.myweekly_app.model.ActivityInfo;

import java.util.List;

public class RequestBody {
    private String user;
    private List<ActivityInfo> activities;

    public RequestBody(String user, List<ActivityInfo> activities) {
        this.user = user;
        this.activities = activities;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<ActivityInfo> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityInfo> activities) {
        this.activities = activities;
    }
}
