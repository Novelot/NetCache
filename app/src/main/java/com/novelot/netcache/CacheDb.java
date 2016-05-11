package com.novelot.netcache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by V on 2016/5/9.
 */
public class CacheDb extends SQLiteOpenHelper {
    public static final String DB_NAME = "kaolafm_cache.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "t_cache";

    public static class Columns {
        public static final String URL = "url";
        public static final String RESULT = "result";
        public static final String ETAG = "etag";
        public static final String LAST_MODIFIED = "last_modified";
        public static final String UPDATE_TIME = "update_time";
    }

    public CacheDb(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer sBuffer = new StringBuffer();
        sBuffer.append("CREATE TABLE [" + TABLE_NAME + "] (");
        sBuffer.append("[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        sBuffer.append(Columns.URL).append(" TEXT,");
        sBuffer.append(Columns.RESULT).append(" TEXT,");
        sBuffer.append(Columns.ETAG).append(" TEXT,");
        sBuffer.append(Columns.LAST_MODIFIED).append(" LONG)");
        sBuffer.append(Columns.UPDATE_TIME).append(" LONG)");
        db.execSQL(sBuffer.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
