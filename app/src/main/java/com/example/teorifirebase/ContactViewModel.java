package com.example.teorifirebase;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;

public class ContactViewModel extends AndroidViewModel {
    private FirebaseRepository repository;
    private MutableLiveData<List<ContactModel>> allContacts;
    private MutableLiveData<String> message;

    public ContactViewModel(@NonNull Application application) {
        super(application);
        repository = new FirebaseRepository();
        allContacts = repository.getContactsLiveData();
        message = repository.getMessageLiveData();

        // Initialize data loading
        repository.init();
    }

    // Insert contact
    public void insert(ContactModel contact) {
        repository.insertContact(contact);
    }

    // Update contact
    public void update(ContactModel contact) {
        repository.updateContact(contact);
    }

    // Delete contact
    public void delete(ContactModel contact) {
        repository.deleteContact(contact);
    }

    // Delete all contacts
    public void deleteAllContacts() {
        repository.deleteAllContacts();
    }

    // Get all contacts
    public LiveData<List<ContactModel>> getAllContacts() {
        return allContacts;
    }

    // Get messages (for success/error feedback)
    public LiveData<String> getMessage() {
        return message;
    }

    // Refresh contacts
    public void refreshContacts() {
        repository.loadAllContacts();
    }
}