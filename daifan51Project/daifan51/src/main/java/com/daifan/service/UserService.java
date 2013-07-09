package com.daifan.service;

import android.content.Context;
import android.util.Log;

import com.daifan.R;
import com.daifan.dao.UserDao;
import com.daifan.domain.LoginResult;
import com.daifan.domain.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 浩 on 13-7-5.
 */
public class UserService {

    private UserDao userDao;

    public UserService(Context context) {
        userDao=new UserDao(context);
    }

    public User getCurrUser() {
        if (isLoggedIn()) {
            User u = userDao.getUser();
            if (u.getId() != null && !u.getId().equals(""))
                return u;
        }

        return null;
    }

    public User login(String email, String password) {
        final String url = String.format("http://51daifan.sinaapp.com/api/login?email=%s&password=%s",email.trim(),password);
        HttpHeaders requestHeaders = new HttpHeaders();
        List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        requestHeaders.setAccept(acceptableMediaTypes);

        HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter2());

        ResponseEntity<LoginResult> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                LoginResult.class);

        Log.d("51daifan", "LoginResult:" + responseEntity.getBody());

        User u = responseEntity.getBody().getUser();
        if(responseEntity.getBody().getSuccess() == 1
                && u != null
                && email.trim().equals(u.getEmail())){
            userDao.addUser(u);
            return u;
        }

        return null;
    }

    public User register(String name, String email, String password) {
        if(name.equals("rh")){
            User user = new User("1", "michaelrong", email, "2013-6-21");
            userDao.addUser(user);
            return user;
        }
        return null;
    }

    public boolean isLoggedIn() {
        int count = userDao.getRowCount();
        return count > 0;
    }

    public boolean logout() {
        userDao.resetTables();
        return true;
    }
}
