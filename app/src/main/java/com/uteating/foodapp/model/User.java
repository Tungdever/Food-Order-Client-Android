package com.uteating.foodapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {
    private String userId;
    private String fullName;
    private String email;
    private String avatarURL;
    private String username;
    private String birthDate;
    private String phone;
    private boolean admin;





    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    private boolean isAdmin;

    public User() {
    }

    public User(String userId, String email, String avatarURL, String userName, String birthDate, String phoneNumber) {
        this.userId = userId;
        this.email = email;
        this.avatarURL = avatarURL;
        this.username = userName;
        this.birthDate = birthDate;
        this.phone = phoneNumber;
    }

    public User(String userId, String fullName, String email, String avatarURL, String username, String birthDate, String phone) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.avatarURL = avatarURL;
        this.username = username;
        this.birthDate = birthDate;
        this.phone = phone;
    }
    public User(String userId, String fullName, String username, String email, String avatarURL,String phone, boolean isAdmin) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.avatarURL = avatarURL;
        this.username = username;
        this.phone = phone;
        this.admin = isAdmin;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoneNumber() {
        return phone;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phone = phoneNumber;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
