package com.daifan.domain;

/**
 * Created by 浩 on 13-7-6.
 */
public class Post {

    private String id;
    private String userName;
    private String userId;
    private String thumbnailUrl;
    private String desc;
    private String createdAt;
    private String address;

    public Post() {
    }

    public Post(String id, String userName, String userId, String thumbnailUrl, String desc, String createdAt, String address) {
        this.id = id;
        this.userName = userName;
        this.userId = userId;
        this.thumbnailUrl = thumbnailUrl;
        this.desc = desc;
        this.createdAt = createdAt;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
