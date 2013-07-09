package com.daifan.service;

import android.util.Log;

import com.daifan.MainActivity;
import com.daifan.domain.Post;
import com.daifan.domain.PostContainer;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.daifan.service.MappingJackson2HttpMessageConverter2;
import org.springframework.web.client.RestTemplate;

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
        HttpHeaders requestHeaders = new HttpHeaders();
        List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        requestHeaders.setAccept(acceptableMediaTypes);

        HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter2());

        ResponseEntity<PostContainer> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
                PostContainer.class);

        return responseEntity.getBody().getPosts();
    }

    public boolean postNew(String countStr, String eatDateStr, String nameStr, String descStr, String currUid) {
        Log.d(MainActivity.DAIFAN_TAG, String.format("postNew count=%s, eatDate=%s, name=%s, desc=%s, uid=%s"
                , countStr, eatDateStr, nameStr, descStr, currUid));

        String params = String.format("count=%s&eatDate=%s&name=%s&desc=%s&uid=%s", countStr, eatDateStr, nameStr, descStr, currUid);
        String url = REST_API + "/post?" + params;


        HttpHeaders requestHeaders = new HttpHeaders();
        List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        requestHeaders.setAccept(acceptableMediaTypes);

        HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter2());

        ResponseEntity<PostContainer> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
                PostContainer.class);

        Log.d(MainActivity.DAIFAN_TAG, String.format("postNew res %s", responseEntity.toString()));

        return 1 == responseEntity.getBody().getSuccess();
    }
}
