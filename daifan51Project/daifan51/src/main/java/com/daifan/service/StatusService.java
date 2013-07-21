package com.daifan.service;

import com.daifan.domain.Status;

import java.util.ArrayList;

/**
 * Created by ronghao on 13-7-21.
 */
public class StatusService {

    public ArrayList<Status> getStatuses(){
        ArrayList<Status> statuses =new ArrayList<Status>();
        for(int i=0;i<6;i++){
            String id = String.valueOf( i);
            Status status=new Status(Integer.parseInt(id),Status.FEED_TYPE_BOOK,"yingke dasha 21","2013-07-19 09:09:01",0,"very good","2013-07-19 09:09:01",1,"good food",
                    "http://51daifan-images.stor.sinaapp.com/recipe/4593376ff295356ac0831ad8f1e58e29_thumb.jpg",
                    "http://51daifan-images.stor.sinaapp.com/recipe/4593376ff295356ac0831ad8f1e58e29.jpg",
                    6,4,2,1,"michaelrong"+id,"2013-07-19 09:09:01");
            statuses.add(status);
        }
        return statuses;
    }
    public ArrayList<Status> getLatestStatuses(int latestId){
        ArrayList<Status> statuses =new ArrayList<Status>();
        for(int i=0;i<2;i++){
            String id = String.valueOf(latestId + i);
            Status status=new Status(Integer.parseInt(id),Status.FEED_TYPE_CREATE,"yingke dasha 21","2013-07-19 09:09:01",0,null,null,1,"good food",
                    "http://51daifan-images.stor.sinaapp.com/recipe/4593376ff295356ac0831ad8f1e58e29_thumb.jpg",
                    "http://51daifan-images.stor.sinaapp.com/recipe/4593376ff295356ac0831ad8f1e58e29.jpg",
                    6,4,2,1,"michaelrong"+id,"2013-07-19 09:09:01");
            statuses.add(status);
        }
        return statuses;
    }

    public ArrayList<Status> getOldestPosts(int oldestId){
        ArrayList<Status> statuses =new ArrayList<Status>();
        for(int i=0;i<2;i++){
            String id = String.valueOf(oldestId - i);
            Status status=new Status(Integer.parseInt(id),Status.FEED_TYPE_CREATE,"yingke dasha 21","2013-07-19 09:09:01",0,null,null,1,"good food",
                    "http://51daifan-images.stor.sinaapp.com/recipe/4593376ff295356ac0831ad8f1e58e29_thumb.jpg",
                    "http://51daifan-images.stor.sinaapp.com/recipe/4593376ff295356ac0831ad8f1e58e29.jpg",
                    6,4,2,1,"michaelrong"+id,"2013-07-19 09:09:01");
            statuses.add(status);
        }
        return statuses;
    }
}
