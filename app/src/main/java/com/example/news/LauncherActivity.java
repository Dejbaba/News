package com.example.news;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LauncherActivity extends AppCompatActivity {

    private static final String TAG = "LauncherActivity";

    private Button adminBtn, userBtn;
    private AlertDialog dialog;


    //firebase
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        //initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        adminBtn = findViewById(R.id.adminBtn);
        userBtn = findViewById(R.id.userBtn);

        adminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent user = new Intent(LauncherActivity.this, UserActivity.class);
                startActivity(user);
            }
        });
    }

    private void showDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(LauncherActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_login, null);
        final EditText mEmail = view.findViewById(R.id.Email);
        final EditText mPassword = view.findViewById(R.id.Password);
        final ProgressBar progressBar = view.findViewById(R.id.progressbar);
        Button loginBtn = view. findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (!mEmail.getText().toString().isEmpty() && !mPassword.getText().toString().isEmpty()){
                    String email = mEmail.getText().toString();
                    String password = mPassword.getText().toString();

                    login(email, password, progressBar);
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LauncherActivity.this, "Please fill all required fields above", Toast.LENGTH_SHORT).show();

                }
            }
        });
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    private void login(String email, String password, final ProgressBar progressBar){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            progressBar.setVisibility(View.INVISIBLE);
                            Intent admin = new Intent(LauncherActivity.this, AdminActivity.class);
                            startActivity(admin);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LauncherActivity.this, "Login failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
