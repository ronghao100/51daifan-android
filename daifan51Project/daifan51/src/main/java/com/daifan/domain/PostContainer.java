package com.daifan.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;

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

    @JsonProperty("bookedUidNames")
    private HashMap<Integer, String> bookedUidNames = new HashMap<Integer, String>(0);

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

    public HashMap<Integer, String> getBookedUidNames() {
        return bookedUidNames;
    }

    public void setBookedUidNames(HashMap<Integer, String> bookedUidNames) {
        this.bookedUidNames = bookedUidNames;
    }

    @Override
    public String toString() {
        return "PostContainer{" +
                "bookedUidNames=" + bookedUidNames +
                ", success=" + success +
                ", error=" + error +
                ", posts=" + posts +
                '}';
    }
}
