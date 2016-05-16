package com.novelot.netcache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

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
//        Cursor c = db.query(CacheOpenHelper.TABLE_NAME, null, CacheOpenHelper.Columns.URL + "=?", new String[]{url}, null, null, null);
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
//        Cursor c = db.query(CacheOpenHelper.TABLE_NAME, null, CacheOpenHelper.Columns.URL + "=?", new String[]{url}, null, null, null);
//        if (c != null) {
//            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
//                result = c.getString(c.getColumnIndex(CacheOpenHelper.Columns.RESULT));
//                break;
//            }
//            c.close();
//        }
//        return result;
//    }


    public void quest(String uri, Callback<String> callback, boolean isCache) {
        if (isCache) {
            Log.v(TAG, "cache=true,get from cache first.");
            Cursor cursor = mContext.getContentResolver().query(CacheProvider.URI, null, CacheOpenHelper.Columns.URL + "=?", new String[]{uri}, null);
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
            questFormNet(uri, callback);
        }
    }

    private void questFromNetAndCache(final String uri, final Callback<String> callback) {
        questFormNet(uri, new Callback<String>() {
            @Override
            public void onFaiure(Exception e) {
                if (callback != null) {
                    callback.onFaiure(e);
                }
            }

            @Override
            public void onSuccess(String s) {
                ContentValues values = new ContentValues();
                values.put(CacheOpenHelper.Columns.URL, uri);
                values.put(CacheOpenHelper.Columns.RESULT, s);
                saveToDb(values);
                //
                if (callback != null) {
                    callback.onSuccess(s);
                }
            }
        });
    }

    private void saveToDb(ContentValues values) {
        mContext.getContentResolver().insert(CacheProvider.URI, values);
    }

    public void questFormNet(String uri, final Callback callback) {
        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(7000);
                if (callback != null) {
                    callback.onSuccess("json data");
                }
            }
        }.start();

    }
}
