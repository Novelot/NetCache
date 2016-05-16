package com.novelot.netcache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by V on 2016/5/9.
 */
public class CacheOpenHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "kaolafm_cache.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "t_cache";

    public interface Columns extends BaseColumns {
        String URL = "url";
        String RESULT = "result";
        String ETAG = "etag";
        String LAST_MODIFIED = "last_modified";
        String UPDATE_TIME = "update_time";
    }

    public CacheOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer sBuffer = new StringBuffer();
        sBuffer.append("CREATE TABLE " + TABLE_NAME + " (");
        sBuffer.append(Columns._ID).append(" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        sBuffer.append(Columns._COUNT).append(" INTEGER,");
        sBuffer.append(Columns.URL).append(" TEXT UNIQUE,");
        sBuffer.append(Columns.RESULT).append(" TEXT,");
        sBuffer.append(Columns.ETAG).append(" TEXT,");
        sBuffer.append(Columns.LAST_MODIFIED).append(" LONG,");
        sBuffer.append(Columns.UPDATE_TIME).append(" LONG");

        sBuffer.append(");");
        db.execSQL(sBuffer.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
