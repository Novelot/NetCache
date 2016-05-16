package com.novelot.netcache;

/**
 * Created by 刘云龙 on 2016/5/16.
 */
public interface Callback<T> {
    void onSuccess(T t);

    void onFaiure(Exception e);
}
