package com.example.teorifirebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private ContactViewModel contactViewModel;
    private List<ContactModel> contactList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private Button btnAdd, btnLogout;

    public static final int ADD_CONTACT_REQUEST = 100;
    public static final int EDIT_CONTACT_REQUEST = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Check if user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User not logged in, redirect to login
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupViewModel();
        setupClickListeners();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.btnAdd);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupClickListeners() {
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
            startActivityForResult(intent, ADD_CONTACT_REQUEST);
        });

        btnLogout.setOnClickListener(v -> logout());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ContactAdapter(new ArrayList<>(), new ContactAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                ContactModel contact = contactList.get(position);

                Intent intent = new Intent(MainActivity.this, EditContactActivity.class);
                intent.putExtra("NAME", contact.getName());
                intent.putExtra("EMAIL", contact.getEmail());
                intent.putExtra("PHONE", contact.getPhone());
                intent.putExtra("ID", contact.getId());
                startActivityForResult(intent, EDIT_CONTACT_REQUEST);
            }

            @Override
            public void onDeleteClick(int position) {
                ContactModel contactToDelete = contactList.get(position);
                contactViewModel.delete(contactToDelete);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        // Initialize ViewModel
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);

        // Observe contacts
        contactViewModel.getAllContacts().observe(this, new Observer<List<ContactModel>>() {
            @Override
            public void onChanged(List<ContactModel> contacts) {
                contactList = contacts != null ? contacts : new ArrayList<>();
                adapter.setContacts(contactList);

                if (contactList.isEmpty()) {
                    Toast.makeText(MainActivity.this, "No contacts found. Add your first contact!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Observe messages (success/error feedback)
        contactViewModel.getMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                if (message != null && !message.isEmpty()) {
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void logout() {
        mAuth.signOut();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Navigate back to login
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == ADD_CONTACT_REQUEST) {
                // Handle add contact
                String name = data.getStringExtra("NAME");
                String email = data.getStringExtra("EMAIL");
                String phone = data.getStringExtra("PHONE");

                if (name != null && email != null && phone != null) {
                    ContactModel newContact = new ContactModel(name, email, phone);
                    contactViewModel.insert(newContact);
                }

            } else if (requestCode == EDIT_CONTACT_REQUEST) {
                // Check if this is a delete operation
                if (data.getBooleanExtra("DELETE", false)) {
                    String id = data.getStringExtra("ID");
                    if (id != null) {
                        // Find the contact with this ID and delete it
                        for (ContactModel contact : contactList) {
                            if (contact.getId().equals(id)) {
                                contactViewModel.delete(contact);
                                break;
                            }
                        }
                    }
                    return;
                }

                // Handle edit contact
                String name = data.getStringExtra("NAME");
                String email = data.getStringExtra("EMAIL");
                String phone = data.getStringExtra("PHONE");
                String id = data.getStringExtra("ID");

                if (id == null || id.isEmpty()) {
                    Toast.makeText(this, "Contact can't be updated!", Toast.LENGTH_SHORT).show();
                    return;
                }

                ContactModel updatedContact = new ContactModel(name, email, phone);
                updatedContact.setId(id);
                contactViewModel.update(updatedContact);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh contacts when returning to MainActivity
        if (contactViewModel != null) {
            contactViewModel.refreshContacts();
        }
    }
}