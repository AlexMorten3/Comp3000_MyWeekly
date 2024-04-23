package com.example.myweekly_app.model;

public class MongoActivity {
    private String name;
    private String category;
    private String duration;
    private boolean isWeekend;
    private int timeOfDay;

    public MongoActivity(String name, String category, String duration, boolean isWeekend, int timeOfDay) {
        this.name = name;
        this.category = category;
        this.duration = duration;
        this.isWeekend = isWeekend;
        this.timeOfDay = timeOfDay;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDuration() {
        return duration;
    }

    public boolean getIsWeekend() {
        return isWeekend;
    }

    public int getTimeOfDay() {
        return timeOfDay;
    }
}
