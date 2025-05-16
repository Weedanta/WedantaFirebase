package com.example.teorifirebase;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditContactActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPhone;
    private Button btnSave, btnDelete;
    private String contactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editContact), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        Intent intent = getIntent();
        String name = intent.getStringExtra("NAME");
        String email = intent.getStringExtra("EMAIL");
        String phone = intent.getStringExtra("PHONE");
        contactId = intent.getStringExtra("ID"); // Now String instead of int

        if (contactId == null || contactId.isEmpty()) {
            Toast.makeText(this, "Error: No valid contact ID found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etName.setText(name);
        etEmail.setText(email);
        etPhone.setText(phone);

        btnSave.setOnClickListener(v -> {
            String updatedName = etName.getText().toString().trim();
            String updatedEmail = etEmail.getText().toString().trim();
            String updatedPhone = etPhone.getText().toString().trim();

            if (updatedName.isEmpty() || updatedEmail.isEmpty() || updatedPhone.isEmpty()) {
                Toast.makeText(EditContactActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("NAME", updatedName);
            resultIntent.putExtra("EMAIL", updatedEmail);
            resultIntent.putExtra("PHONE", updatedPhone);
            resultIntent.putExtra("ID", contactId);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        btnDelete.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("ID", contactId);
            resultIntent.putExtra("DELETE", true);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}