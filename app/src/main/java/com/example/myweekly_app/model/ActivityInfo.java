package com.example.myweekly_app.model;

import com.example.myweekly_app.helper.TimeConverters;

public class ActivityInfo {
    private long id;
    private String name;
    private int state;
    private String day;
    private String startTime;
    private String endTime;

    public ActivityInfo(String name, int state, String day, String startTime, String endTime) {
        this.name = name;
        this.state = state;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getFullTime() {
        String startTimeStr = TimeConverters.convertTimeStringToFormat(startTime);
        String endTimeStr = TimeConverters.convertTimeStringToFormat(endTime);

        return startTimeStr + " - " + endTimeStr;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStart() {
        return startTime;
    }

    public void setStart(String startTime) {
        this.startTime = startTime;
    }

    public String getEnd() {
        return endTime;
    }

    public void setEnd(String endTime) {
        this.endTime = endTime;
    }
}

