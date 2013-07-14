package com.daifan.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by ronghao on 13-7-7.
 */
public class PostContainer extends ApiInvokeResult {

    @JsonProperty("posts")
    private ArrayList<Post> posts;

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }
}
