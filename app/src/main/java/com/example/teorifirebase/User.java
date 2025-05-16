package com.example.teorifirebase;

import java.util.Date;

    public class User {
        private String uid;
        private String fullName;
        private String email;
        private Date createdAt;
        private Date lastLogin;
        private boolean isEmailVerified;

        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        public User() {
        }

        public User(String uid, String fullName, String email) {
            this.uid = uid;
            this.fullName = fullName;
            this.email = email;
            this.createdAt = new Date();
            this.lastLogin = new Date();
            this.isEmailVerified = false;
        }

        // Getters
        public String getUid() {
            return uid;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmail() {
            return email;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public Date getLastLogin() {
            return lastLogin;
        }

        public boolean isEmailVerified() {
            return isEmailVerified;
        }

        // Setters
        public void setUid(String uid) {
            this.uid = uid;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

        public void setLastLogin(Date lastLogin) {
            this.lastLogin = lastLogin;
        }

        public void setEmailVerified(boolean emailVerified) {
            isEmailVerified = emailVerified;
        }

        @Override
        public String toString() {
            return "User{" +
                    "uid='" + uid + '\'' +
                    ", fullName='" + fullName + '\'' +
                    ", email='" + email + '\'' +
                    ", createdAt=" + createdAt +
                    ", lastLogin=" + lastLogin +
                    ", isEmailVerified=" + isEmailVerified +
                    '}';
        }
    }

