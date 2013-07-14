package com.daifan.push;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by 浩 on 13-7-14.
 */
public class BindResponse {

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("response_params")
    public BindParams bindParams;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public BindParams getBindParams() {
        return bindParams;
    }

    public void setBindParams(BindParams bindParams) {
        this.bindParams = bindParams;
    }

    public class BindParams {

        @JsonProperty("appid")
        private String appId;
        @JsonProperty("user_id")
        private String userId;
        @JsonProperty("channel_id")
        private String channelId;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }
    }
}
