package com.example.nsbmlectureattendance;

import android.util.Patterns;

public class emailValidate {

//    Validate emails
    public static boolean isValidEmail(CharSequence target) {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
