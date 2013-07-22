package com.daifan.domain;

import android.text.TextUtils;
import android.util.Log;
import com.daifan.Singleton;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    //TODO: timezone 保持服务器端一致

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+08:00")
    @JsonProperty("eatDate")
    private Date eatDate;
    @JsonProperty("updatedAt")
    private String updatedAt;
    @JsonProperty("createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+08:00")
    private Date createdAt;
    @JsonProperty("address")
    private String address;

    @JsonProperty("images")
    private List<String> images = new ArrayList<String>();

    @JsonProperty("image1")
    private String image1;
    @JsonProperty("image2")
    private String image2;
    @JsonProperty("image3")
    private String image3;
    @JsonProperty("image4")
    private String image4;
    @JsonProperty("image5")
    private String image5;
    @JsonProperty("image6")
    private String image6;

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

    public Date getEatDate() {
        return eatDate;
    }

    public void setEatDate(Date eatDate) {
        this.eatDate = eatDate;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getImages() {
        if (images == null || images.size() == 0) {
            if (images == null)
                images = new ArrayList<String>();
            this.addImage(image1);
            this.addImage(image2);
            this.addImage(image3);
            this.addImage(image4);
            this.addImage(image5);
            this.addImage(image6);
        }

        return this.images;
    }

    private void addImage(String p) {
        if (p != null && p.trim().length() > 0)
            this.images.add(p);
    }

    public static void main(String[] args) {
        System.out.println(fullImage("http://51daifan-images.stor.sinaapp.com/recipe/177f27ec2b4db53f1244c626fb01ed13_thumb.jpg"));
    }

    public static String fullImage(String path) {
        return path.replaceFirst("_thumb.jpg", ".jpg");
    }

    public void setImages(List<String> images) {
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
            System.arraycopy(this.bookedUids, 0, s, 0, currLen);
            s[currLen] = currU.getId();
            this.bookedUids = s;
            Singleton.getInstance().addCommentUidNames(currU);
            this.bookedCount++;
        }
    }

    public String getBookedUNames() {
        String[] uNames = new String[this.bookedUids.length];
        for (int i = 0; i < bookedUids.length; i++) {
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
                ", images=" + images +
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
                else {
                    this.bookedCount--;
                }
            }
        } else {
            Log.e(Singleton.DAIFAN_TAG, "Current user is null, failed to undo booking " + this.getId());
        }

        this.bookedUids = newb.toArray(new String[newb.size()]);
    }

    public boolean outofOrder() {
        return this.count <= this.bookedCount
                || this.isInactive();
    }

    public Comment addComment(int uid, String comment) {
        Comment comm = null;
        if (comments != null)
            for (Comment c : this.comments)
                if (c.getUid() == uid) {
                    comm = c;
                    break;
                }

        if (comm == null) {
            comm = new Comment(uid, comment);
            this.comments.add(comm);
        } else
            comm.setComment(comment);

        return comm;
    }

    public int getLeft() {
        int left = this.count - this.bookedCount;
        return left > 0 ? left : 0;
    }

    public boolean isInactive() {
        //TODO:需要处理已经过期的订单，不能依靠本地时间
        return this.eatDate == null || this.eatDate.before(new Date());
    }

    public boolean hasImage() {
        return !getImages().isEmpty();
    }

    public ArrayList<String> fullImages() {

        ArrayList<String> fullImages = new ArrayList<String>();
        for (String s : this.getImages()) {
            fullImages.add(fullImage(s));
        }
        return fullImages;
    }

    public String myComment(String currUid) {

        if (currUid != null) {
            int uid;
            try {
                uid = Integer.parseInt(currUid);
            } catch (NumberFormatException e) {
                return "";
            }
            for (Comment c : comments) {
                if (c.getUid() == uid)
                    return c.getComment();
            }
        }

        return "";
    }
}
