package com.example.myweekly_app.model;

public class StaticActivityInfo {
    private long id;
    private static String name;
    private static String day;
    private static String startTime;
    private static String endTime;
    private static int state;

    public StaticActivityInfo(String name, String day, String startTime, String endTime, int state) {
        this.name = name;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.state = state;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public static String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public static String getStart() {
        return startTime;
    }

    public void setStart(String startTime) {
        this.startTime = startTime;
    }

    public static String getEnd() {
        return endTime;
    }

    public void setEnd(String endTime) {
        this.endTime = endTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "StaticActivityInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", day='" + day + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", state=" + state +
                '}';
    }
}
