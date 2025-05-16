package com.example.teorifirebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    // UI components
    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private TextView tvSignIn;
    private CheckBox cbTerms;
    private ProgressBar progressBar;

    // Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        initViews();

        // Set click listeners
        setClickListeners();
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvSignIn = findViewById(R.id.tvSignIn);
        cbTerms = findViewById(R.id.cbTerms);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setClickListeners() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }

    private void createUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
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

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Please confirm your password");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please agree to the Terms and Conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);
        btnSignUp.setEnabled(false);

        // Create user with Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                // Update user profile with display name
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(fullName)
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressBar.setVisibility(View.GONE);
                                                btnSignUp.setEnabled(true);

                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User profile updated.");
                                                }

                                                Toast.makeText(SignUpActivity.this,
                                                        "Account created successfully!",
                                                        Toast.LENGTH_SHORT).show();

                                                // Navigate to MainActivity
                                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                            }
                        } else {
                            // Sign up failed
                            progressBar.setVisibility(View.GONE);
                            btnSignUp.setEnabled(true);

                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            String errorMessage = "Registration failed.";

                            if (task.getException() != null) {
                                String exceptionMessage = task.getException().getMessage();
                                if (exceptionMessage != null) {
                                    if (exceptionMessage.contains("email address is already in use")) {
                                        errorMessage = "This email is already registered";
                                    } else if (exceptionMessage.contains("email address is badly formatted")) {
                                        errorMessage = "Invalid email format";
                                    } else if (exceptionMessage.contains("weak password")) {
                                        errorMessage = "Password is too weak";
                                    }
                                }
                            }

                            Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            finish();
        }
    }
}