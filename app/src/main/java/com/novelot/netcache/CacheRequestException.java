package com.novelot.netcache;

/**
 * Created by 刘云龙 on 2016/5/31.
 */
public class CacheRequestException extends Exception {
    private int errorCode;
    private String errorInfo;

    public CacheRequestException(int code) {
        errorCode = code;
    }

    public CacheRequestException(String s) {
        errorInfo = s;
    }
}
