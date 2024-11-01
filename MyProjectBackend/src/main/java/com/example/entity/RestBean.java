package com.example.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

public record RestBean<T>(int code, T data, String msg) {
    public static<T> RestBean<T> success(T data){
        return new RestBean<T>(200,data,"success");

    }

    public static<T> RestBean<T> success(){
        return success(null);
    }


    public static<T> RestBean<T> failure(int code,String msg){
        return new RestBean<T>(code,null,msg);

    }

    public String asJsonString(){
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);

    }
}
