package com.daifan.service;

import android.util.Log;

import com.daifan.Singleton;
import com.daifan.domain.Comment;
import com.daifan.domain.Post;
import com.daifan.domain.PostContainer;

import com.daifan.domain.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class PostService {

    public static final String REST_API = "http://51daifan.sinaapp.com/api";

    public ArrayList<Post> getPosts() {
        final String url = REST_API + "/posts?type=0";
        return getInternalPosts(url);
    }

    public ArrayList<Post> getLatestPosts(int latestId) {
        final String url = REST_API + "/posts?type=1&currentId=" + latestId;
        return getInternalPosts(url);
    }

    public ArrayList<Post> getOldestPosts(int oldestId) {
        final String url = REST_API + "/posts?type=2&currentId=" + oldestId;
        return getInternalPosts(url);
    }

    private ArrayList<Post> getInternalPosts(String url) {

        try {
            ResponseEntity<PostContainer> responseEntity = httpGet(url, PostContainer.class);

            PostContainer resp = responseEntity.getBody();

            Singleton.getInstance().addCommentUidNames(resp.getBookedUidNames());

            return resp.getPosts();
        } catch (RestClientException e) {
            Log.e(Singleton.DAIFAN_TAG, "get posts failed for url " + url, e);
            return new ArrayList<Post>();
        }
    }

    public boolean book(Post post) {

        User u = Singleton.getInstance().getCurrUser();

        String params = String.format("postId=%s&food_owner_id=%s&food_owner_name=%s&userId=%s&userName=%s"
                ,post.getId(), post.getUserId(), post.getUserName(), u.getId(), u.getName());

        String url = REST_API + "/book?" + params;

        try {
            ResponseEntity<PostContainer> responseEntity = httpGet(url, PostContainer.class);
            return 1 == responseEntity.getBody().getSuccess();
        } catch (Exception e) {
            Log.e(Singleton.DAIFAN_TAG, "post failed:" + e.getMessage(), e);
            return false;
        }
    }
    public boolean undoBook(Post post) {

        User u = Singleton.getInstance().getCurrUser();

        String params = String.format("postId=%s&userId=%s", post.getId(), u.getId());

        String url = REST_API + "/undo-book?" + params;

        try {
            ResponseEntity<PostContainer> responseEntity = httpGet(url, PostContainer.class);
            return 1 == responseEntity.getBody().getSuccess();
        } catch (Exception e) {
            Log.e(Singleton.DAIFAN_TAG, "post failed:" + e.getMessage(), e);
            return false;
        }
    }

    public boolean postNew(String countStr, String eatDateStr, String nameStr, String descStr, String currUid) {
        Log.d(Singleton.DAIFAN_TAG, String.format("postNew count=%s, eatDate=%s, name=%s, desc=%s, uid=%s"
                , countStr, eatDateStr, nameStr, descStr, currUid));

        String params = String.format("count=%s&eatDate=%s&name=%s&desc=%s&uid=%s", countStr, eatDateStr, nameStr, descStr, currUid);
        String url = REST_API + "/post?" + params;

        ResponseEntity<PostContainer> responseEntity = httpGet(url, PostContainer.class);
        return 1 == responseEntity.getBody().getSuccess();
    }

    private <T> ResponseEntity<T> httpGet(String url, Class<T> responseType) {

        Log.d(Singleton.DAIFAN_TAG, url);

        //TODO: url encoding for names
        HttpHeaders requestHeaders = new HttpHeaders();
        List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        requestHeaders.setAccept(acceptableMediaTypes);

        HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter2());

        ResponseEntity<T> rtn = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
                responseType);

        Log.d(Singleton.DAIFAN_TAG, "response:" + rtn);

        return rtn;
    }

    public Comment postComment(Post post, int currUid, String comment) {

        Log.d(Singleton.DAIFAN_TAG, "postComment to post " + post.getId() + " from uid " + currUid);
        Singleton.getInstance().addCommentUidNames(Singleton.getInstance().getCurrUser());

        //TODO: could run in background
        //TODO: replace uid with uid in session!
        String url = null;
        try {
            url = new StringBuffer().append(REST_API).append("/comment?")
                    .append("userId=").append(currUid)
                    .append("&postId=").append(post.getId())
                    .append("&comment=").append(URLEncoder.encode(comment, "UTF-8"))
                    .toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        boolean ok = false;
        try {
            ok = (1 == this.httpGet(url, PostContainer.class).getBody().getSuccess());
        } catch (Exception e) {
           Log.e(Singleton.DAIFAN_TAG, "error when post comment", e);
        }
        Log.d(Singleton.DAIFAN_TAG, "postComment "+comment+" result: " + ok);

        return post.addComment(currUid, comment);
    }
}
