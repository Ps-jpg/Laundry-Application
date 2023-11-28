package com.example.foodex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Button btnLogIn;
    EditText etEmail, etPassword;
    TextView tvSign;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvSign = findViewById(R.id.tvSign);
        btnLogIn = findViewById(R.id.btnLogIn);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        progressBar = findViewById(R.id.progressBar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser mFirebasebaseUser = mFirebaseAuth.getCurrentUser();
            if (mFirebasebaseUser != null) {
                Toast.makeText(MainActivity.this, "Connection to Firebase Successful", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, Home.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Connection to Firebase Failed", Toast.LENGTH_LONG).show();
            }
        };

        tvSign.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUp.class);
            startActivity(intent);
        });

        btnLogIn.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Disable UI interaction
            etEmail.setEnabled(false);
            etPassword.setEnabled(false);
            btnLogIn.setEnabled(false);
            tvSign.setEnabled(false);

            progressBar.setVisibility(View.VISIBLE);

            mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                progressBar.setVisibility(View.GONE);

                // Enable UI interaction
                etEmail.setEnabled(true);
                etPassword.setEnabled(true);
                btnLogIn.setEnabled(true);
                tvSign.setEnabled(true);

                if (!task.isSuccessful()) {
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(MainActivity.this, "Login Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, Home.class);
                    startActivity(intent);
                }
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}