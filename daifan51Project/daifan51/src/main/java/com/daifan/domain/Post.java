package com.daifan.domain;

import android.text.TextUtils;
import com.daifan.Singleton;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    private int count;
    @JsonProperty("bookedCount")
    private int bookedCount;
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

    @JsonProperty("comments")
    private List<Comment> comments = new ArrayList<Comment>();

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getBookedCount() {
        return bookedCount;
    }

    public void setBookedCount(int bookedCount) {
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

    public String[] getBookedUids() {
        return bookedUids;
    }

    public void setBookedUids(String[] bookedUids) {
        this.bookedUids = bookedUids;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
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

    public void addBooked(User currU) {
        if (!booked(currU.getId())) {
            int currLen = this.bookedUids.length;
            String[] s = new String[currLen + 1];
            s[currLen] = currU.getId();
            this.bookedUids = s;
            Singleton.getInstance().addCommentUidNames(currU);
        }
    }

    public String getBookedUNames() {
        String[] uNames = new String[this.bookedUids.length];
        for(int i = 0; i < bookedUids.length; i++ ) {
            uNames[i] = Singleton.getInstance().getUNameById(bookedUids[i]);
        }
        return TextUtils.join(", ", uNames);
    }

    @Override
    public String toString() {
        return "Post{" +
                "address='" + address + '\'' +
                ", id=" + id +
                ", userName='" + userName + '\'' +
                ", userId=" + userId +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", count='" + count + '\'' +
                ", bookedCount='" + bookedCount + '\'' +
                ", eatDate='" + eatDate + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", createdAt=" + createdAt +
                ", images=" + Arrays.toString(images) +
                ", bookedUids=" + Arrays.toString(bookedUids) +
                ", comments=" + comments +
                '}';
    }

    public void undoBook(User currU) {
        ArrayList<String> newb = new ArrayList<String>();
        if (currU != null) {
            for (String uid : bookedUids) {
                if (!currU.getId().equals(uid))
                    newb.add(uid);
            }
        }

        this.bookedUids = newb.toArray(new String[newb.size()]);
    }

    public boolean outofOrder() {
        return this.count <= this.bookedCount;
    }

    public Comment addComment(int uid, String comment) {
        Comment comm = new Comment(uid, comment);
        this.comments.add(comm);
        return comm;
    }
}
