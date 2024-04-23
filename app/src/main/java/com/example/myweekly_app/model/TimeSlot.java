package com.example.myweekly_app.model;

public class TimeSlot {
    private int startTime;
    private int endTime;
    private String day;

    public TimeSlot(int startTime, int endTime, String day) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public String getDay() {
        return day;
    }
}
