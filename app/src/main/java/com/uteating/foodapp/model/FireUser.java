package com.uteating.foodapp.model;

import java.io.Serializable;

public class FireUser implements Serializable {
    private String userId;
    private String email;

    public FireUser(String userId, String email) {
        this.userId = userId;
        this.email = email;
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
}
