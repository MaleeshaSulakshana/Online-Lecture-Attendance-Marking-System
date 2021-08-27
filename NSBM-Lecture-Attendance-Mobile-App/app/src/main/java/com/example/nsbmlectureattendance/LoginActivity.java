package com.example.nsbmlectureattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    TextView txtCreateAccountPage;
    TextInputLayout txtInputBoxEmail, txtInputBoxPsw;

    boolean doubleBackToExitPressedOnce = false;
    private Button btnExitYes, btnExitNo, btnLogin;
    private Dialog exitDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        Get layouts
        exitDialog = new Dialog(LoginActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        txtCreateAccountPage = (TextView) findViewById(R.id.txtCreateAccountPage);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        mAuth = FirebaseAuth.getInstance();

//        Go to crate account page
        txtCreateAccountPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivity(login);
                finish();
            }
        });

//        Login button activity
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Text edit assign to variables
                txtInputBoxEmail = (TextInputLayout) findViewById(R.id.txtEmail);
                txtInputBoxPsw = (TextInputLayout) findViewById(R.id.txtPsw);

                String semail = txtInputBoxEmail.getEditText().getText().toString();
                String spsw = txtInputBoxPsw.getEditText().getText().toString();

//                validates
                if (semail.isEmpty() || spsw.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();
                }
                else if (!emailValidate.isValidEmail(semail)) {
                    Toast.makeText(LoginActivity.this, "Your email not validate", Toast.LENGTH_SHORT).show();
                }
                else {

//                    Start Progress bar
                    loadingBar = new ProgressDialog(LoginActivity.this);
                    loadingBar.setTitle("Waiting for login");
                    loadingBar.setMessage("Please Wait");
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();

//                    For login
                    mAuth.signInWithEmailAndPassword(semail, spsw)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    loadingBar.dismiss();
                                    Intent home = new Intent(LoginActivity.this, AttendanceActivity.class);
                                    startActivity(home);
                                    finish();
                                }
                                else {
                                    loadingBar.dismiss();
                                    Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
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
}