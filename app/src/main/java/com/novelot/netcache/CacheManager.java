package com.novelot.netcache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by V on 2016/5/10.
 */
public class CacheManager {
    private static final String TAG = "novelot";
    private static CacheManager mInstance = new CacheManager();
    private Context mContext;

    private CacheManager() {
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
//    public boolean contains(Context context, String url) {
//        boolean result = false;
//        CacheOpenHelper cacheDb = new CacheOpenHelper(context);
//        SQLiteDatabase db = cacheDb.getReadableDatabase();
//        Cursor c = db.query(CacheOpenHelper.TABLE_NAME, null, CacheOpenHelper.Columns.URI + "=?", new String[]{url}, null, null, null);
//        if (c != null) {
//            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
//                result = true;
//                break;
//            }
//            c.close();
//        }
//        return result;
//    }
//
//    public String getResult(Context context, String url) {
//        String result = null;
//        CacheOpenHelper cacheDb = new CacheOpenHelper(context);
//        SQLiteDatabase db = cacheDb.getReadableDatabase();
//        Cursor c = db.query(CacheOpenHelper.TABLE_NAME, null, CacheOpenHelper.Columns.URI + "=?", new String[]{url}, null, null, null);
//        if (c != null) {
//            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
//                result = c.getString(c.getColumnIndex(CacheOpenHelper.Columns.RESULT));
//                break;
//            }
//            c.close();
//        }
//        return result;
//    }


    public void quest(String uri, final Callback<String> callback, boolean isCache) {
        if (isCache) {
            Log.v(TAG, "cache=true,get from cache first.");
            Cursor cursor = mContext.getContentResolver().query(CacheProvider.URI, null, CacheOpenHelper.Columns.URI + "=?", new String[]{uri}, null);
            if (cursor != null) {
                String result = null;
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    result = cursor.getString(cursor.getColumnIndex(CacheOpenHelper.Columns.RESULT));
                    break;
                }

                if (!TextUtils.isEmpty(result)) {
                    Log.v(TAG, "cache result != null,return result.");
                    callback.onSuccess(result);
                } else {
                    Log.v(TAG, "cache result = null,get from net then cache.");
                    questFromNetAndCache(uri, callback);
                }
            } else {
                Log.v(TAG, "cache result = null,get from net then cache.");
                questFromNetAndCache(uri, callback);
            }
        } else {
            Log.v(TAG, "cache=false,get from net only.");
            questFormNet(uri, new Callback<CacheRequest>() {
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

    private void questFromNetAndCache(final String uri, final Callback<String> callback) {
        questFormNet(uri, new Callback<CacheRequest>() {
            @Override
            public void onFaiure(Exception e) {
                if (callback != null) {
                    callback.onFaiure(e);
                }
            }

            @Override
            public void onSuccess(CacheRequest s) {
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

    private void saveToDb(ContentValues values) {
        mContext.getContentResolver().insert(CacheProvider.URI, values);
    }

    public void questFormNet(final String uri, final Callback<CacheRequest> callback) {
        new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        Map<String, List<String>> headerFields = conn.getHeaderFields();
                        CacheRequest request = new CacheRequest();
                        request.uri = uri;
                        request.etag = headerFields.get("ETag").get(0);
                        request.lastModified = Utils.turnGMTTime(headerFields.get("Last-Modified").get(0));
                        request.updateTime = System.currentTimeMillis();
                        request.result = "Json result";
                        if (callback != null) {
                            callback.onSuccess(request);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFaiure(new CacheRequestException(code));
                        }
                    }

                } catch (IOException e) {
                    if (callback != null) {
                        callback.onFaiure(e);
                    }
                }

            }
        }.start();

    }
}
