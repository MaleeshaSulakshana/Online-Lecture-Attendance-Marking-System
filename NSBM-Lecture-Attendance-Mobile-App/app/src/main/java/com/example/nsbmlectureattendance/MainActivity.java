package com.example.nsbmlectureattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    private Button btnStart, btnExitYes, btnExitNo;
    private Dialog exitDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Get layouts
        exitDialog = new Dialog(MainActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        mAuth = FirebaseAuth.getInstance();

//        Set btnStart code
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                    Check is connect network
                if (isNetworkConnected() == false) {
                    Toast.makeText(MainActivity.this, "Please turn on wifi or mobile data!", Toast.LENGTH_SHORT).show();

//                    Check internet connection
                } else if (isInternetAvailable() == false){
                    Toast.makeText(MainActivity.this, "Please check your internet connection!", Toast.LENGTH_SHORT).show();

//                    Check is logged in or not
                } else {

                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent nextPage = new Intent(MainActivity.this, AttendanceActivity.class);
                                startActivity(nextPage);
                                finish();
                            }
                        },50);

                    }
                    else {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent login = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(login);
                                finish();
                            }
                        },50);

                    }

                }


            }
        });

    }

//    Hide status bar and navigation bar
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


//    Tap to close app
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        exitDialog.show();

        btnExitYes = (Button) exitDialog.findViewById(R.id.btnYes);
        btnExitYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        btnExitNo = (Button) exitDialog.findViewById(R.id.btnNo);
        btnExitNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });

    }

//    Check network connection
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

//    Check internet connection
    private boolean isInternetAvailable() {
        try {
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                InetAddress ipAddr = InetAddress.getByName("www.google.com");
                return !ipAddr.equals("");
            }
            else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
    }
}