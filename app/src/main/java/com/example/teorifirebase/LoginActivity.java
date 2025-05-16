package com.example.teorifirebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.widget.Button;
import android.widget.EditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // UI components
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignUp, tvForgotPassword;
    private ProgressBar progressBar;

    // Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        initViews();

        // Set click listeners
        setClickListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // Sign in with Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Hide progress bar
                        progressBar.setVisibility(View.GONE);
                        btnLogin.setEnabled(true);

                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                            // Navigate to MainActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            // Sign in failed
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            String errorMessage = "Authentication failed.";

                            if (task.getException() != null) {
                                String exceptionMessage = task.getException().getMessage();
                                if (exceptionMessage != null) {
                                    if (exceptionMessage.contains("password is invalid")) {
                                        errorMessage = "Invalid password";
                                    } else if (exceptionMessage.contains("no user record")) {
                                        errorMessage = "No account found with this email";
                                    } else if (exceptionMessage.contains("email address is badly formatted")) {
                                        errorMessage = "Invalid email format";
                                    }
                                }
                            }

                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void resetPassword() {
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Enter your email to reset password");
            etEmail.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    "Password reset email sent to " + email,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    "Failed to send reset email. Please check your email address.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already logged in, navigate to MainActivity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}