package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class RestBean<T> {
    private T msg;
    private int status;
    private boolean success;

    public RestBean(T msg, int status, boolean success) {
        this.msg = msg;
        this.status = status;
        this.success = success;
    }

    private RestBean(){}

    public static<T> RestBean<T> success(){
        return new RestBean<>(null,200,true);
    }

    public static<T> RestBean<T> success(T data){
        return new RestBean<>(data,200,true);
    }


    public static<T> RestBean<T> failure(int code){
        return new RestBean<>(null,code,false);
    }

    public static<T> RestBean<T> failure(int code,T data){
        return new RestBean<>(data,code,false);
    }
}
