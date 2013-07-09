package com.daifan.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class LoginResult {

    @JsonProperty("success")
    private int success;
    @JsonProperty("error")
    private int error;
    @JsonProperty("user")
    private User user;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
