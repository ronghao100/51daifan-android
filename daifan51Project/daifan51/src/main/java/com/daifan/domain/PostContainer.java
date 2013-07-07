package com.daifan.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by ronghao on 13-7-7.
 */
public class PostContainer {

    @JsonProperty("success")
    private int success;
    @JsonProperty("error")
    private int error;
    @JsonProperty("posts")
    private ArrayList<Post> posts;

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

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }
}
