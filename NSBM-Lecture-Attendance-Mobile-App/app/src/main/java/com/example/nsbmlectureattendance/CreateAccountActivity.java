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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccountActivity extends AppCompatActivity {

    TextView txtLoginPage;

    private TextInputLayout id ,name, email, batch, psw, cpsw;

    boolean doubleBackToExitPressedOnce = false;
    private Button btnExitYes, btnExitNo, btnCreateAccount;
    private Dialog exitDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

//        Get layouts
        exitDialog = new Dialog(CreateAccountActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        txtLoginPage = (TextView) findViewById(R.id.txtLoginPage);
        btnCreateAccount = (Button) findViewById(R.id.btnCreateAccount);

        mAuth = FirebaseAuth.getInstance();

        txtLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(CreateAccountActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
            }
        });


        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Text edit assign to variables
                id = (TextInputLayout) findViewById(R.id.txtId);
                name = (TextInputLayout) findViewById(R.id.txtName);
                email = (TextInputLayout) findViewById(R.id.txtEmail);
                batch = (TextInputLayout) findViewById(R.id.txtBatch);
                psw = (TextInputLayout) findViewById(R.id.txtPsw);
                cpsw = (TextInputLayout) findViewById(R.id.txtCPsw);

//                Cast to strings
                String sid = id.getEditText().getText().toString();
                String sname = name.getEditText().getText().toString();
                String semail = email.getEditText().getText().toString();
                String sbatch = batch.getEditText().getText().toString();
                String spsw = psw.getEditText().getText().toString();
                String scpsw = cpsw.getEditText().getText().toString();

//                Validate and create account
                if (sid.isEmpty() || sname.isEmpty() || semail.isEmpty() || sbatch.isEmpty() || spsw.isEmpty() || scpsw.isEmpty()) {
                    Toast.makeText(CreateAccountActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();
                }
                else if (!emailValidate.isValidEmail(semail)) {
                    Toast.makeText(CreateAccountActivity.this, "Your email not validate", Toast.LENGTH_SHORT).show();
                }
                else if (spsw.length() < 6 ) {
                    Toast.makeText(CreateAccountActivity.this, "Your password not strong", Toast.LENGTH_SHORT).show();
                }
                else if (!spsw.equals(scpsw)) {
                    Toast.makeText(CreateAccountActivity.this, "Confirm password not matched!", Toast.LENGTH_SHORT).show();
                }
                else {

//                      Start Progress bar
                    loadingBar = new ProgressDialog(CreateAccountActivity.this);
                    loadingBar.setTitle("Creating new Account");
                    loadingBar.setMessage("Please Wait");
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();

//                    Create Account using email and password
                    mAuth.createUserWithEmailAndPassword(semail, spsw)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(CreateAccountActivity.this, "Account Create Successful", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

//                                      Get user id
                                        String userId = mAuth.getCurrentUser().getUid();

                                        reference = FirebaseDatabase.getInstance().getReference("students/"+userId);
                                        InsertAccountDetails insertAccountDetails = new InsertAccountDetails();

                                        insertAccountDetails.setId(sid);
                                        insertAccountDetails.setName(sname);
                                        insertAccountDetails.setEmail(semail);
                                        insertAccountDetails.setBatch(sbatch);

                                        reference.setValue(insertAccountDetails);

                                        Intent attendee = new Intent(CreateAccountActivity.this, AttendanceActivity.class);
                                        startActivity(attendee);
                                        finish();
                                    }
                                    else {
                                        String msg = task.getException().toString();
                                        Toast.makeText(CreateAccountActivity.this, "Error" + msg, Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
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