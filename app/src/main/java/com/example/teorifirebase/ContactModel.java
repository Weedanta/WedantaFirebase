package com.example.teorifirebase;

import androidx.annotation.NonNull;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class ContactModel {
    @DocumentId
    private String id;

    @NonNull
    private String name;

    @NonNull
    private String email;

    @NonNull
    private String phone;

    @ServerTimestamp
    private Date createdAt;

    private String userId; // To associate contacts with specific users

    // Default constructor required for Firestore
    public ContactModel() {
    }

    public ContactModel(@NonNull String name, @NonNull String email, @NonNull String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public ContactModel(@NonNull String name, @NonNull String email, @NonNull String phone, String userId) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userId = userId;
    }

    // Getters
    public String getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    @NonNull
    public String getPhone() {
        return phone;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getUserId() {
        return userId;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    public void setPhone(@NonNull String phone) {
        this.phone = phone;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ContactModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", createdAt=" + createdAt +
                ", userId='" + userId + '\'' +
                '}';
    }
}