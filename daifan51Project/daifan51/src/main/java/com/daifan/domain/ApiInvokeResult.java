package com.daifan.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by 浩 on 13-7-14.
 */
public class ApiInvokeResult {

    @JsonProperty("success")
    public int success;
    @JsonProperty("error")
    public int error;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }
}
