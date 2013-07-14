package com.daifan.domain;

import com.fasterxml.jackson.annotation.JsonProperty;


public class LoginResult extends ApiInvokeResult {

    @JsonProperty("user")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
