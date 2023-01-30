package com.sample.flixtracker;

public class GlobalData {
    public static String userId;
    public static Integer userAge;

    public static void setUserID(String userID){
        userId = userID;  // Note that the original userId has 'd' in lowercase, while the userID in argument has uppercase 'D'
    }

    public static String getUserId(){
        return userId;
    }

    public static void setUserAge(Integer age){
        userAge = age;
    }

    public static Integer getUserAge(){
        return userAge;
    }
}
