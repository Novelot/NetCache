package com.novelot.netcache;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by V on 2016/5/10.
 */
public class CacheManager {
    private static CacheManager mInstance = new CacheManager();

    private CacheManager() {
    }

    public static CacheManager getInstance() {
        return mInstance;
    }

    public boolean contains(Context context, String url) {
        boolean result = false;
        CacheOpenHelper cacheDb = new CacheOpenHelper(context);
        SQLiteDatabase db = cacheDb.getReadableDatabase();
        Cursor c = db.query(CacheOpenHelper.TABLE_NAME, null, CacheOpenHelper.Columns.URL + "=?", new String[]{url}, null, null, null);
        if (c != null) {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = true;
                break;
            }
            c.close();
        }
        return result;
    }

    public String getResult(Context context, String url) {
        String result = null;
        CacheOpenHelper cacheDb = new CacheOpenHelper(context);
        SQLiteDatabase db = cacheDb.getReadableDatabase();
        Cursor c = db.query(CacheOpenHelper.TABLE_NAME, null, CacheOpenHelper.Columns.URL + "=?", new String[]{url}, null, null, null);
        if (c != null) {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(c.getColumnIndex(CacheOpenHelper.Columns.RESULT));
                break;
            }
            c.close();
        }
        return result;
    }
}
