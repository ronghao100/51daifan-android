package com.daifan.domain;

import java.io.Serializable;

/**
 * Created by ronghao on 13-7-21.
 * user feeds include:
 * 1.create a food info
 * 2.book a food
 * 3.comment a food
 */
public class Status implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int FEED_TYPE_CREATE = 0;
    public static final int FEED_TYPE_BOOK = 1;

    public int id;
    public int type;
    public String address;
    public String createdAt;

    /** if type=1 below 3 fields have value**/
    public int orderId;
    public String comment;
    public String bookedAt;

    public int foodId;
    public String text;
    /** 缩略图. */
    public String thumbnailPic;
    /** 原始图片. */
    public String originalPic;
    public int count;
    public int bookedCount;
    public int commentCount;
    public int userId;
    public String userName;
    public String postedAt;

    public Status(int id, int type, String address, String createdAt, int orderId, String comment, String bookedAt, int foodId, String text, String thumbnailPic, String originalPic, int count, int bookedCount, int commentCount, int userId, String userName, String postedAt) {
        this.id = id;
        this.type = type;
        this.address = address;
        this.createdAt = createdAt;
        this.orderId = orderId;
        this.comment = comment;
        this.bookedAt = bookedAt;
        this.foodId = foodId;
        this.text = text;
        this.thumbnailPic = thumbnailPic;
        this.originalPic = originalPic;
        this.count = count;
        this.bookedCount = bookedCount;
        this.commentCount = commentCount;
        this.userId = userId;
        this.userName = userName;
        this.postedAt = postedAt;
    }
}
