package com.novelot.netcache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by 刘云龙 on 2016/5/10.
 */
public class CacheManager {
    private static final String TAG = "novelot";
    private static CacheManager mInstance = new CacheManager();
    private Context mContext;
    private static BlockingQueue<NetRequest> queue = new LinkedBlockingQueue<NetRequest>();
    private static Thread loopThread;

    private CacheManager() {
        if (loopThread == null) {
            loopThread = new Thread() {
                @Override
                public void run() {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    while (true) {
                        try {
                            NetRequest task = queue.take();
                            task.exe();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            loopThread.start();
        }
    }

    public static CacheManager getInstance() {
        return mInstance;
    }

    public CacheManager init(Context c) {
        if (mContext == null) {
            mContext = c;
        }
        return mInstance;
    }

    /**
     * 回收，关闭
     */
    public void release() {
        loopThread.interrupt();
        loopThread = null;
    }

    /**
     * 请求
     *
     * @param uri       请求地址
     * @param callback  回调
     * @param isCache   是否缓存
     * @param cacheTime 毫秒
     */
    public void quest(String uri, final Callback<String> callback, boolean isCache, long cacheTime) {
        if (isCache) {
            Log.v(TAG, "cache=true,get from cache first.");
            Cursor cursor = mContext.getContentResolver().query(CacheProvider.URI, null, CacheOpenHelper.Columns.URI + "=?", new String[]{uri}, null);
            if (cursor != null) {
                String result = null;
                long updateTime = 0L;
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    updateTime = cursor.getLong(cursor.getColumnIndex(CacheOpenHelper.Columns.UPDATE_TIME));
                    result = cursor.getString(cursor.getColumnIndex(CacheOpenHelper.Columns.RESULT));
                    break;
                }
                if (Utils.isOutCacheTime(updateTime, cacheTime)) {
                    Log.v(TAG, "cache time out,quest new date form net ,then save.");
                    questFromNetAndCache(uri, callback);
                } else {
                    if (!TextUtils.isEmpty(result)) {
                        Log.v(TAG, "cache result != null,return result.");
                        callback.onSuccess(result);
                    } else {
                        Log.v(TAG, "cache result = null,get from net then cache.");
                        questFromNetAndCache(uri, callback);
                    }
                }
                cursor.close();
            } else {
                Log.v(TAG, "cache result = null,get from net then cache.");
                questFromNetAndCache(uri, callback);
            }
        } else {
            Log.v(TAG, "cache=false,get from net only.");
            addNetRequestQueue(uri, new Callback<CacheRequest>() {
                @Override
                public void onSuccess(CacheRequest e) {
                    if (callback != null) {
                        callback.onSuccess(e.result);
                    }
                }

                @Override
                public void onFaiure(Exception e) {
                    if (callback != null) {
                        callback.onFaiure(e);
                    }
                }
            });
        }
    }

    /**
     * 从网络请求并缓存
     *
     * @param uri
     * @param callback
     */
    private void questFromNetAndCache(final String uri, final Callback<String> callback) {
        addNetRequestQueue(uri, new Callback<CacheRequest>() {
            @Override
            public void onFaiure(Exception e) {
                if (callback != null) {
                    callback.onFaiure(e);
                }
            }

            @Override
            public void onSuccess(CacheRequest s) {
                Log.v(TAG, "questFromNetAndCache:" + s.toString());
                ContentValues values = new ContentValues();
                values.put(CacheOpenHelper.Columns.URI, s.uri);
                values.put(CacheOpenHelper.Columns.RESULT, s.result);
                values.put(CacheOpenHelper.Columns.ETAG, s.etag);
                values.put(CacheOpenHelper.Columns.LAST_MODIFIED, s.lastModified);
                values.put(CacheOpenHelper.Columns.UPDATE_TIME, s.updateTime);
                saveToDb(values);
                //
                if (callback != null) {
                    callback.onSuccess(s.result);
                }
            }
        });
    }

    /**
     * 保存到数据库
     *
     * @param values
     */
    private void saveToDb(ContentValues values) {
        mContext.getContentResolver().insert(CacheProvider.URI, values);
    }


    /**
     * 加入请求队列
     *
     * @param uri
     * @param callback
     */
    public void addNetRequestQueue(String uri, Callback<CacheRequest> callback) {
        NetRequest task = new NetRequest(uri, callback);
        if (!queue.contains(task)) {
            try {
                queue.put(task);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Queue contains this NetRequest");
        }
    }
}
