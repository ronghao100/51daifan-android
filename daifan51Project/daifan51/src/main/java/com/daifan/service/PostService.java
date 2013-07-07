package com.daifan.service;

import com.daifan.domain.Post;
import com.daifan.domain.PostContainer;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class PostService {

    public ArrayList<Post> getPosts() {
        final String url = "http://51daifan.sinaapp.com/api/posts?type=0";
        return getInternalPosts(url);
    }

    public ArrayList<Post> getLatestPosts(int latestId) {
        final String url = "http://51daifan.sinaapp.com/api/posts?type=1&currentId=" + latestId;
        return getInternalPosts(url);
    }

    public ArrayList<Post> getOldestPosts(int oldestId) {
        final String url = "http://51daifan.sinaapp.com/api/posts?type=2&currentId=" + oldestId;
        return getInternalPosts(url);
    }

    private ArrayList<Post> getInternalPosts(String url) {
        HttpHeaders requestHeaders = new HttpHeaders();
        List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        requestHeaders.setAccept(acceptableMediaTypes);

        HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        ResponseEntity<PostContainer> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
                PostContainer.class);

        return responseEntity.getBody().getPosts();
    }
}
