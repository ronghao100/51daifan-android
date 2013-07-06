package com.daifan.service;

import com.daifan.domain.Post;

import java.util.ArrayList;

/**
 * Created by ronghao on 6/23/13.
 */
public class PostService {

    public ArrayList<Post> getPosts(){
        ArrayList<Post> posts=new ArrayList<Post>();
        for(int i=6;i>0;i--){
            Post post=new Post(i,"anchuan"+String.valueOf(i),1,
                    "http://51daifan-avatar.stor.sinaapp.com/9ab7af3fe1c5080440f72813c988bd75_thumb.jpg",
                    "6月25号 带 午后水果 : 周二中午到超市发买些苹果，有需要的同事我可以帮着带一些，价格以超市小票为准：）每天一水果，身体格外好。",
                    "2013-06-22 18:10:11","银科大厦21层");
            posts.add(post);
        }
        return posts;
    }

    public ArrayList<Post> getLatestPosts(int latestId){
        ArrayList<Post> posts=new ArrayList<Post>();
        for(int i=2;i>0;i--){
            String id = String.valueOf(latestId + i);
            Post post=new Post(latestId + i,"heylight"+id,2,
                    "http://51daifan-avatar.stor.sinaapp.com/c0561140a8879293544bfa2906edba90_thumb.jpg",
                    "6月25号 带 午后水果 : 周二中午到超市发买些苹果，有需要的同事我可以帮着带一些，价格以超市小票为准：）每天一水果，身体格外好。",
                    "2013-06-22 18:10:11","银科大厦21层");
            posts.add(post);
        }
        return posts;
    }

    public ArrayList<Post> getOldestPosts(int oldestId){
        ArrayList<Post> posts=new ArrayList<Post>();
        for(int i=1;i<4;i++){
            String id = String.valueOf(oldestId - i);
            Post post=new Post(oldestId - i,"xiting"+id,3,
                    "http://51daifan-avatar.stor.sinaapp.com/810cf3cfd083a52bee85a8cfc7ad7bdf_thumb.jpg",
                    "6月25号 带 午后水果 : 周二中午到超市发买些苹果，有需要的同事我可以帮着带一些，价格以超市小票为准：）每天一水果，身体格外好。",
                    "2013-06-22 18:10:11","银科大厦21层");
            posts.add(post);
        }
        return posts;
    }
}
