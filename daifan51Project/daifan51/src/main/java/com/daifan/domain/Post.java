package com.daifan.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Post {

    @JsonProperty("objectId")
    private int id;
    @JsonProperty("realName")
    private String userName;
    @JsonProperty("user")
    private int userId;
    @JsonProperty("avatarThumbnail")
    private String thumbnailUrl;
    @JsonProperty("name")
    private String name;
    @JsonProperty("describe")
    private String desc;
    @JsonProperty("count")
    private String count;
    @JsonProperty("bookedCount")
    private String bookedCount;
    @JsonProperty("eatDate")
    private String eatDate;
    @JsonProperty("updatedAt")
    private String updatedAt;
    @JsonProperty("createdAt")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd kk:mm:ss")
    private Date createdAt;
    @JsonProperty("address")
    private String address;

    @JsonProperty("images")
    private String[] images = new String[0];

    @JsonProperty("bookedUids")
    private String[] bookedUids = new String[0];

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getBookedCount() {
        return bookedCount;
    }

    public void setBookedCount(String bookedCount) {
        this.bookedCount = bookedCount;
    }

    public String getEatDate() {
        return eatDate;
    }

    public void setEatDate(String eatDate) {
        this.eatDate = eatDate;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String[] getImages() {
        //return images;
        return new String[]{"http://51daifan-images.stor.sinaapp.com/recipe/47d0ad175398780db34e21c0e9623ccf.jpg"};
    }

    public static String thumb(String path) {
        return path.replaceFirst(".jpg", "_thumb.jpg");
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public boolean booked(String currUid) {
        if (currUid != null) {
            for (String uid : bookedUids) {
                if (currUid.equals(uid))
                    return true;
            }
        }

        return false;
    }

    public void addBooked(String currUid) {
        if (!booked(currUid)) {
            int currLen = this.bookedUids.length;
            String[] s = new String[currLen + 1];
            s[currLen] = currUid;
            this.bookedUids = s;
        }
    }
}
