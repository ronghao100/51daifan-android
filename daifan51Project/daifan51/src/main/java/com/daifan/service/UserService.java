package com.daifan.service;

import android.content.Context;

import com.daifan.dao.UserDao;
import com.daifan.domain.User;

/**
 * Created by 浩 on 13-7-5.
 */
public class UserService {

    private UserDao userDao;

    public UserService(Context context) {
        userDao=new UserDao(context);
    }

    public User login(String email, String password) {
        if(email.equals("rh@qq.com")){
            User user = new User("1", "michaelrong", email, "2013-6-21");
            userDao.addUser(user);
            return user;
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

    public boolean isLoggedIn(Context context) {
        int count = userDao.getRowCount();
        return count > 0;
    }

    public boolean logout(Context context) {
        userDao.resetTables();
        return true;
    }
}
