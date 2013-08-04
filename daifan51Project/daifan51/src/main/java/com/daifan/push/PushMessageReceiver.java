package com.daifan.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.baidu.android.pushservice.PushConstants;
import com.daifan.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by ronghao on 13-7-14.
 * push receiver
 */
public class PushMessageReceiver extends BroadcastReceiver {
    /**
     * TAG to Log
     */
    public static final String TAG = PushMessageReceiver.class.getSimpleName();
    private UserService userService;


    /**
     * @param context Context
     * @param intent  接收的intent
     */
    @Override
    public void onReceive(final Context context, Intent intent) {

        Log.d(TAG, ">>> Receive intent: \r\n" + intent);

        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
            //获取消息内容
            String message = intent.getExtras().getString(
                    PushConstants.EXTRA_PUSH_MESSAGE_STRING);

            //消息的用户自定义内容读取方式
            Log.i(TAG, "onMessage: " + message);

            //自定义内容的json串
            Log.d(TAG, "EXTRA_EXTRA = " + intent.getStringExtra(PushConstants.EXTRA_EXTRA));


            //用户在此自定义处理消息,以下代码为demo界面展示用
//            Intent responseIntent = null;
//            responseIntent = new Intent(Utils.ACTION_MESSAGE);
//            responseIntent.putExtra(Utils.EXTRA_MESSAGE, message);
//            responseIntent.setClass(context, PushDemoActivity.class);
//            responseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(responseIntent);

        } else if (intent.getAction().equals(PushConstants.ACTION_RECEIVE)) {
            //处理绑定等方法的返回数据
            //PushManager.startWork()的返回值通过PushConstants.METHOD_BIND得到

            //获取方法
            final String method = intent
                    .getStringExtra(PushConstants.EXTRA_METHOD);
            //方法返回错误码。若绑定返回错误（非0），则应用将不能正常接收消息。
            //绑定失败的原因有多种，如网络原因，或access token过期。
            //请不要在出错时进行简单的startWork调用，这有可能导致死循环。
            //可以通过限制重试次数，或者在其他时机重新调用来解决。
            final int errorCode = intent
                    .getIntExtra(PushConstants.EXTRA_ERROR_CODE,
                            PushConstants.ERROR_SUCCESS);
            //返回内容
            final String content = new String(
                    intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT));

            //用户在此自定义处理消息,以下代码为demo界面展示用
            Log.d(TAG, "onMessage: method : " + method);
            Log.d(TAG, "onMessage: result : " + errorCode);
            Log.d(TAG, "onMessage: content : " + content);

            if (PushConstants.METHOD_BIND.equals(method)) {
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            BindResponse bindResponse = mapper.readValue(content, BindResponse.class);
                            BindResponse.BindParams bindParams = bindResponse.getBindParams();
                            String pushUserId = bindParams.getUserId();
                            String pushChannelId = bindParams.getChannelId();
                            Log.d(TAG, "bindResponse: user_id : " + pushUserId + " channel_id : " + pushChannelId);

                            userService = new UserService(context);
                            userService.pushRegister(pushUserId, pushChannelId);
                            return true;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                }.execute();
                Log.d(TAG, "run bind method at async task...");
            }


//            Intent responseIntent = null;
//            responseIntent = new Intent(Utils.ACTION_RESPONSE);
//            responseIntent.putExtra(Utils.RESPONSE_METHOD, method);
//            responseIntent.putExtra(Utils.RESPONSE_ERRCODE,
//                    errorCode);
//            responseIntent.putExtra(Utils.RESPONSE_CONTENT, content);
//            responseIntent.setClass(context, PushDemoActivity.class);
//            responseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(responseIntent);

        }
    }

}
