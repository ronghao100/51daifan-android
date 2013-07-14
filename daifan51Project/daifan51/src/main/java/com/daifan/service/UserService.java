package com.daifan.service;

import android.content.Context;
import android.util.Log;

import com.daifan.Singleton;
import com.daifan.dao.UserDao;
import com.daifan.domain.ApiInvokeResult;
import com.daifan.domain.LoginResult;
import com.daifan.domain.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ronghao on 13-7-5.
 */
public class UserService {

    public static final String TAG = UserService.class.getSimpleName();

    private UserDao userDao;

    public UserService(Context context) {
        userDao = new UserDao(context);
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
        final String url = String.format("http://51daifan.sinaapp.com/api/login?email=%s&password=%s", email.trim(), password);
        HttpHeaders requestHeaders = getHttpHeaders();

        HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter2());

        ResponseEntity<LoginResult> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                    LoginResult.class);

            Log.d(Singleton.DAIFAN_TAG, "LoginResult:" + responseEntity.getBody());

            User u = responseEntity.getBody().getUser();
            if (responseEntity.getBody().getSuccess() == 1
                    && u != null
                    && email.trim().equals(u.getEmail())) {
                userDao.addUser(u);
                return u;
            }
        } catch (RestClientException e) {
            Log.e(Singleton.DAIFAN_TAG, "failed to post login ", e);
        }

        return null;
    }

    public User register(String name, String email, String password) {
        final String url = String.format("http://51daifan.sinaapp.com/api/register?name=%s&email=%s&password=%s",
                name.trim(), email.trim(), password);
        HttpHeaders requestHeaders = getHttpHeaders();

        HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter2());

        ResponseEntity<LoginResult> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                    LoginResult.class);

            Log.d(TAG, "RegisterResult:" + responseEntity.getBody());

            User u = responseEntity.getBody().getUser();
            if (responseEntity.getBody().getSuccess() == 1
                    && u != null
                    && email.trim().equals(u.getEmail())) {
                userDao.addUser(u);
                return u;
            }
        } catch (RestClientException e) {
            Log.e(TAG, "failed to post register ", e);
        }

        return null;
    }

    public void pushRegister(String pushUserId, String pushChannelId) {
        User user = userDao.getUser();
        String userId = user.getId();

        final String url = String.format("http://51daifan.sinaapp.com/api/push_register?userId=%s&pushUserId=%s&pushChannelId=%s",
                userId, pushUserId.trim(), pushChannelId.trim());
        HttpHeaders requestHeaders = getHttpHeaders();

        HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter2());

        ResponseEntity<ApiInvokeResult> responseEntity = null;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                    ApiInvokeResult.class);

            Log.d(TAG, "ApiInvokeResult:" + responseEntity.getBody());
        } catch (RestClientException e) {
            Log.e(TAG, "failed to post push register ", e);
        }
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders requestHeaders = new HttpHeaders();
        List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        requestHeaders.setAccept(acceptableMediaTypes);
        return requestHeaders;
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
