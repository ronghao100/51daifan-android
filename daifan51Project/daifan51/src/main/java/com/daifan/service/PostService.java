package com.daifan.service;

import android.util.Log;

import com.daifan.Singleton;
import com.daifan.activity.PostNewActivity;
import com.daifan.domain.*;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class PostService {

    public ArrayList<Post> getPosts() {
        final String url = Singleton.REST_API + "/posts?type=0";
        return getInternalPosts(url);
    }

    public ArrayList<Post> getLatestPosts(int latestId) {
        final String url = Singleton.REST_API + "/posts?type=1&currentId=" + latestId;
        return getInternalPosts(url);
    }

    public ArrayList<Post> getOldestPosts(int oldestId) {
        final String url = Singleton.REST_API + "/posts?type=2&currentId=" + oldestId;
        return getInternalPosts(url);
    }

    private ArrayList<Post> getInternalPosts(String url) {

        try {
            ResponseEntity<PostContainer> responseEntity = http(url, PostContainer.class);
            if (responseEntity == null)
                return new ArrayList<Post>();

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

        String url = Singleton.REST_API + "/book?" + params;

        try {
            ResponseEntity<PostContainer> responseEntity = http(url, PostContainer.class);
            return responseEntity != null && Singleton.isSucc(responseEntity.getBody().getSuccess());
        } catch (Exception e) {
            Log.e(Singleton.DAIFAN_TAG, "post failed:" + e.getMessage(), e);
            return false;
        }
    }
    public boolean undoBook(Post post) {

        User u = Singleton.getInstance().getCurrUser();

        String params = String.format("postId=%s&userId=%s", post.getId(), u.getId());

        String url = Singleton.REST_API + "/undo-book?" + params;

        try {
            ResponseEntity<PostContainer> responseEntity = http(url, PostContainer.class);
            return responseEntity != null && Singleton.isSucc(responseEntity.getBody().getSuccess());
        } catch (Exception e) {
            Log.e(Singleton.DAIFAN_TAG, "post failed:" + e.getMessage(), e);
            return false;
        }
    }

    public boolean postNew(String countStr, String eatDateStr, String nameStr, String descStr, String currUid, List<String> imgs) {
        Log.d(Singleton.DAIFAN_TAG, String.format("postNew count=%s, eatDate=%s, name=%s, desc=%s, uid=%s"
                , countStr, eatDateStr, nameStr, descStr, currUid));

        String params = String.format("count=%s&eatDate=%s&name=%s&desc=%s&uid=%s", countStr, eatDateStr, nameStr, descStr, currUid);
        String url = Singleton.REST_API + "/post?" + params;

        MultiValueMap<String, String> imgMap = new LinkedMultiValueMap<String, String>();;

        if (imgs != null && imgs.size()>0) {
            for(String img : imgs)
                imgMap.add("img[]", img);
        }

        ResponseEntity<PostNew> responseEntity = http(url, PostNew.class, imgMap);

        return responseEntity != null
                && Singleton.isSucc(responseEntity.getBody().getSuccess())
                && responseEntity.getBody().getNewPostId()>0;
    }

    private <T> ResponseEntity<T> http(String url, Class<T> responseType) {
        return this.http(url, responseType, null);
    }
    /**
     *
     * @param url
     * @param responseType
     * @param <T>
     * @return null if exception
     */
    private <T> ResponseEntity<T> http(String url, Class<T> responseType, MultiValueMap posts) {

        Log.d(Singleton.DAIFAN_TAG, url);

        //TODO: url encoding for names
        HttpHeaders reqHead = new HttpHeaders();
        List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        reqHead.setAccept(acceptableMediaTypes);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter2());

        try {
            ResponseEntity<T> rtn;
            if (posts != null)  {
                reqHead.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
                rtn = restTemplate.postForEntity(url, new HttpEntity<Object>(posts, reqHead), responseType);
            } else {
                HttpEntity<?> requestEntity = new HttpEntity<Object>(reqHead);
                rtn = restTemplate.exchange(url, HttpMethod.GET, requestEntity, responseType);
            }

            Log.d(Singleton.DAIFAN_TAG, "response:" + rtn);
            return rtn;
        }catch (HttpMessageNotReadableException e){
            Log.e(Singleton.DAIFAN_TAG, "Post service reading message exception", e);
            return null;
        }
    }

    public Comment postComment(Post post, int currUid, String comment) {

        Log.d(Singleton.DAIFAN_TAG, "postComment to post " + post.getId() + " from uid " + currUid);
        Singleton.getInstance().addCommentUidNames(Singleton.getInstance().getCurrUser());

        //TODO: could run in background
        //TODO: replace uid with uid in session!
        String url = null;
        try {
            url = new StringBuffer().append(Singleton.REST_API).append("/comment?")
                    .append("userId=").append(currUid)
                    .append("&postId=").append(post.getId())
                    .append("&comment=").append(URLEncoder.encode(comment, "UTF-8"))
                    .toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        boolean ok = false;
        try {
            ResponseEntity<PostContainer> rtnEntity = this.http(url, PostContainer.class);
            ok = rtnEntity != null && Singleton.isSucc(rtnEntity.getBody().getSuccess());
        } catch (Exception e) {
           Log.e(Singleton.DAIFAN_TAG, "error when post comment", e);
        }
        Log.d(Singleton.DAIFAN_TAG, "postComment "+comment+" result: " + ok);

        return post.addComment(currUid, comment);
    }
}
