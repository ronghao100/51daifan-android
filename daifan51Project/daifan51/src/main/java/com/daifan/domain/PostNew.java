package com.daifan.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostNew extends ApiInvokeResult {
    @JsonProperty("postid")
    private int newPostId;

    public int getNewPostId() {
        return newPostId;
    }

    public void setNewPostId(int newPostId) {
        this.newPostId = newPostId;
    }
}
