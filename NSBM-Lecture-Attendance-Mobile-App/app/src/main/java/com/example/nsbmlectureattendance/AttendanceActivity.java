package com.example.nsbmlectureattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AttendanceActivity extends AppCompatActivity implements LocationListener {

    boolean doubleBackToExitPressedOnce = false;
    private Button btnExitYes, btnExitNo, btnLogoutYes, btnLogoutNo;
    private Dialog exitDialog, logoutDialog;

    protected LocationManager locationManager;
    TextView txtlat, txtlon, textLogout, lecTitle, lecDate, startTime, endTime, textView2;

    Button getLocationBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference reference, attendeeRef, lectureRef, batchRef, rootRef;

    String lat = "", lon = "" ;
    String lectureId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

//        Get layouts
        exitDialog = new Dialog(AttendanceActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        logoutDialog = new Dialog(AttendanceActivity.this);
        logoutDialog.setContentView(R.layout.logout_dialog_box);

        mAuth = FirebaseAuth.getInstance();

//        Get user permission for gps
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        checkLecture();

        txtlat = (TextView) findViewById(R.id.txtlat);
        txtlon = (TextView) findViewById(R.id.txtlon);
        lecTitle = (TextView) findViewById(R.id.lecTitle);
        lecDate = (TextView) findViewById(R.id.date);
        startTime = (TextView) findViewById(R.id.startTime);
        endTime = (TextView) findViewById(R.id.endTime);
        textView2 = (TextView) findViewById(R.id.textView2);
        textLogout = (TextView) findViewById(R.id.txtLogout);
        getLocationBtn = (Button) findViewById(R.id.btnGetLocation);

        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attendToLecture();
            }
        });

//        For logout
        textLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logoutDialog.show();

                btnLogoutYes = (Button) logoutDialog.findViewById(R.id.btnYes);
                btnLogoutYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        mAuth.signOut();
                        Intent login = new Intent(AttendanceActivity.this, LoginActivity.class);
                        startActivity(login);
                        finish();
                    }
                });

                btnLogoutNo = (Button) logoutDialog.findViewById(R.id.btnNo);
                btnLogoutNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logoutDialog.dismiss();
                    }
                });

            }
        });

    }

//    Function for attend to lecture
    void attendToLecture() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            Location location= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            String userID = mAuth.getCurrentUser().getUid();

            if (location != null) {

                txtlat.setText(""+location.getLatitude());
                txtlon.setText(""+location.getLongitude());

                System.out.println("getLatitude "+location.getLatitude()+" getLongitude "+location.getLongitude());

                lat = txtlat.getText().toString();
                lon = txtlon.getText().toString();

                if (lat != "" && lon != "") {

                    if (lectureId != "") {

//                    Get lectures
                    rootRef = FirebaseDatabase.getInstance().getReference("lectures/"+lectureId);
                    rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild("attendees")) {

//                                Check attendees
                                rootRef = FirebaseDatabase.getInstance().getReference("lectures/"+lectureId+"/attendees/");
                                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.hasChild(userID)) {

//                                            Add data to attendee
                                            updateAttendance(userID);

                                        } else {

                                            Toast.makeText(AttendanceActivity.this, "You already attended to the lecture", Toast.LENGTH_SHORT).show();

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } else {
//                                Add data to attendee
                                updateAttendance(userID);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    }

                }


            }


        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }


//    Function for update attendance
    private void updateAttendance(String userID) {

        reference = FirebaseDatabase.getInstance().getReference("students/"+userID);
        attendeeRef = FirebaseDatabase.getInstance().getReference("lectures/"+lectureId+"/attendees/"+userID);

        classAttendee attendee = new classAttendee();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                attendee.setId(snapshot.child("id").getValue().toString());
                attendee.setName(snapshot.child("name").getValue().toString());
                attendee.setEmail(snapshot.child("email").getValue().toString());
                attendee.setLatitude(lat);
                attendee.setLongitude(lon);
                attendeeRef.setValue(attendee);
                Toast.makeText(AttendanceActivity.this, "You attended your lecture", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


//    Function for check lectures
    private void checkLecture() {

        String userID = mAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference("students/"+userID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String batch = snapshot.child("batch").getValue().toString();

                batchRef = FirebaseDatabase.getInstance().getReference("lectures/");
                batchRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot lectureSnapshot) {

                        for (DataSnapshot snapshot1 : lectureSnapshot.getChildren()) {

                            lectureRef = FirebaseDatabase.getInstance().getReference("lectures/"+snapshot1.getKey());
                            lectureRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot lecSnapshot) {

                                    if (lecSnapshot.child("batch").getValue().toString().equals(batch)) {
                                        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                                        if (date.equals(lecSnapshot.child("lectureDate").getValue().toString())) {
                                            final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                                            String time = dateFormat.format(new Date());

                                            if (lecSnapshot.child("lectureTime").getValue().toString().compareTo(time) < 0
                                            && lecSnapshot.child("lectureEnd").getValue().toString().compareTo(time) > 0) {

                                                lecTitle.setText(lecSnapshot.child("lectureTitle").getValue().toString());
                                                lecDate.setText(lecSnapshot.child("lectureDate").getValue().toString());
                                                startTime.setText(lecSnapshot.child("lectureTime").getValue().toString());
                                                endTime.setText(lecSnapshot.child("lectureEnd").getValue().toString());
                                                lectureId = lecSnapshot.child("lectureId").getValue().toString();
                                                getLocationBtn.setVisibility(View.VISIBLE);

                                            }

                                        }

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                textView2.setText("Your haven't lecture now");
            }
        });

    }


    @Override
    public void onLocationChanged(Location location) {
//        locationText.setText("Current Location: " + location.getLatitude() + ", " + location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(AttendanceActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

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
}