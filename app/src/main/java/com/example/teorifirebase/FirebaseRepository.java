package com.example.teorifirebase;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FirebaseRepository {
    private static final String TAG = "FirebaseRepository";
    private static final String COLLECTION_CONTACTS = "contacts";

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private MutableLiveData<List<ContactModel>> contactsLiveData;
    private MutableLiveData<String> messageLiveData;

    public FirebaseRepository() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        contactsLiveData = new MutableLiveData<>();
        messageLiveData = new MutableLiveData<>();
    }

    // Get current user ID
    private String getCurrentUserId() {
        FirebaseUser currentUser = auth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : null;
    }

    // Insert new contact
    public void insertContact(ContactModel contact) {
        String userId = getCurrentUserId();
        if (userId == null) {
            messageLiveData.setValue("User not authenticated");
            return;
        }

        contact.setUserId(userId);

        db.collection(COLLECTION_CONTACTS)
                .add(contact)
                .addOnSuccessListener(new OnSuccessListener<com.google.firebase.firestore.DocumentReference>() {
                    @Override
                    public void onSuccess(com.google.firebase.firestore.DocumentReference documentReference) {
                        Log.d(TAG, "Contact added with ID: " + documentReference.getId());
                        messageLiveData.setValue("Contact added successfully");
                        loadAllContacts(); // Refresh data
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding contact", e);
                        messageLiveData.setValue("Failed to add contact: " + e.getMessage());
                    }
                });
    }

    // Update contact
    public void updateContact(ContactModel contact) {
        if (contact.getId() == null) {
            messageLiveData.setValue("Contact ID is null");
            return;
        }

        String userId = getCurrentUserId();
        if (userId == null) {
            messageLiveData.setValue("User not authenticated");
            return;
        }

        contact.setUserId(userId);

        db.collection(COLLECTION_CONTACTS)
                .document(contact.getId())
                .set(contact)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Contact updated successfully");
                        messageLiveData.setValue("Contact updated successfully");
                        loadAllContacts(); // Refresh data
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating contact", e);
                        messageLiveData.setValue("Failed to update contact: " + e.getMessage());
                    }
                });
    }

    // Delete contact
    public void deleteContact(ContactModel contact) {
        if (contact.getId() == null) {
            messageLiveData.setValue("Contact ID is null");
            return;
        }

        db.collection(COLLECTION_CONTACTS)
                .document(contact.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Contact deleted successfully");
                        messageLiveData.setValue("Contact deleted successfully");
                        loadAllContacts(); // Refresh data
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting contact", e);
                        messageLiveData.setValue("Failed to delete contact: " + e.getMessage());
                    }
                });
    }

    // Load all contacts for current user - NO ORDERBY TO AVOID INDEX
    public void loadAllContacts() {
        String userId = getCurrentUserId();
        if (userId == null) {
            messageLiveData.setValue("User not authenticated");
            contactsLiveData.setValue(new ArrayList<>());
            return;
        }

        // Simple query - NO orderBy to avoid composite index requirement
        db.collection(COLLECTION_CONTACTS)
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<ContactModel> contacts = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ContactModel contact = document.toObject(ContactModel.class);
                                contact.setId(document.getId());
                                contacts.add(contact);
                                Log.d(TAG, "Contact loaded: " + contact.getName());
                            }

                            // Sort on client side by name (alphabetical) to avoid timestamp issues
                            Collections.sort(contacts, new Comparator<ContactModel>() {
                                @Override
                                public int compare(ContactModel c1, ContactModel c2) {
                                    if (c1.getName() == null && c2.getName() == null) return 0;
                                    if (c1.getName() == null) return 1;
                                    if (c2.getName() == null) return -1;
                                    return c1.getName().compareToIgnoreCase(c2.getName());
                                }
                            });

                            contactsLiveData.setValue(contacts);
                            Log.d(TAG, "Successfully loaded " + contacts.size() + " contacts");

                            if (contacts.size() > 0) {
                                messageLiveData.setValue("Contacts loaded successfully");
                            }

                        } else {
                            Log.w(TAG, "Error getting contacts", task.getException());
                            messageLiveData.setValue("Failed to load contacts: " +
                                    (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                            contactsLiveData.setValue(new ArrayList<>());
                        }
                    }
                });
    }

    // Delete all contacts for current user
    public void deleteAllContacts() {
        String userId = getCurrentUserId();
        if (userId == null) {
            messageLiveData.setValue("User not authenticated");
            return;
        }

        // Simple query - NO orderBy
        db.collection(COLLECTION_CONTACTS)
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                            }
                            messageLiveData.setValue("All contacts deleted");
                            loadAllContacts(); // Refresh data
                        } else {
                            Log.w(TAG, "Error deleting all contacts", task.getException());
                            messageLiveData.setValue("Failed to delete all contacts");
                        }
                    }
                });
    }

    // Get LiveData for contacts
    public MutableLiveData<List<ContactModel>> getContactsLiveData() {
        return contactsLiveData;
    }

    // Get LiveData for messages
    public MutableLiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    // Initialize data (load contacts when repository is created)
    public void init() {
        loadAllContacts();
    }
}