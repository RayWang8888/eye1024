package com.eye1024.bean;

import com.google.gson.Gson;

/**
 * Created by Administrator on 2015/6/29.
 */
public class WebResult {
    private String errcode;
    private String errmsg;

    public static WebResult parse(String json){
        return new Gson().fromJson(json,WebResult.class);
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

}
