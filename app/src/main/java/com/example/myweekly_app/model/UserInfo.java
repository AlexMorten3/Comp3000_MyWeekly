package com.example.myweekly_app.model;

public class UserInfo {

    private static String email;

    public static String getEmail() {
        return email;
    }

    public void signOut() {
        this.email = null;
    }

}
