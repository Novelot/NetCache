package com.novelot.netcache;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;

public class CacheProvider extends ContentProvider {
    public static final String AUTHORITIES = "com.kaolafm.cache.CacheProvider";
    public static final Uri URI = Uri.parse("content://" + AUTHORITIES + "/" + CacheOpenHelper.TABLE_NAME);
    private static final UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int MATCH_CODE_CACHE = 1;
    private CacheOpenHelper mOpenHelper;

    static {
        sMatcher.addURI(AUTHORITIES, CacheOpenHelper.TABLE_NAME, MATCH_CODE_CACHE);
    }

    public CacheProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;
        switch (sMatcher.match(uri)) {
            case MATCH_CODE_CACHE:
                count = db.delete(CacheOpenHelper.TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            default:
                break;
        }

        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (sMatcher.match(uri)) {
            case MATCH_CODE_CACHE:
//                return "vnd.android.cursor.dir/vnd." + AUTHORITIES + "." + CacheDb.TABLE_NAME; // one row
                return "vnd.android.cursor.dir/vnd." + CacheOpenHelper.TABLE_NAME; // one row
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sMatcher.match(uri)) {
            case MATCH_CODE_CACHE:
                try {
                    String sql = SqlUtils.createSqlInsert("INSERT OR REPLACE INTO ", CacheOpenHelper.TABLE_NAME, new String[]{
                            CacheOpenHelper.Columns.URI, CacheOpenHelper.Columns.RESULT, CacheOpenHelper.Columns.ETAG,
                            CacheOpenHelper.Columns.LAST_MODIFIED, CacheOpenHelper.Columns.UPDATE_TIME
                    });
                    SQLiteStatement insertOrReplaceStatement = db.compileStatement(sql);
                    insertOrReplaceStatement.bindAllArgsAsStrings(new String[]{
                            values.getAsString(CacheOpenHelper.Columns.URI),
                            values.getAsString(CacheOpenHelper.Columns.RESULT),
                            values.getAsString(CacheOpenHelper.Columns.ETAG),
                            values.getAsString(CacheOpenHelper.Columns.LAST_MODIFIED),
                            values.getAsString(CacheOpenHelper.Columns.UPDATE_TIME),
                    });
                    insertOrReplaceStatement.execute();
                    getContext().getContentResolver().notifyChange(uri, null);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

        return uri;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new CacheOpenHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sMatcher.match(uri)) {
            case MATCH_CODE_CACHE:
                cursor = db.rawQuery("SELECT * FROM " + CacheOpenHelper.TABLE_NAME, null);
                Context context = getContext();
                if (null != context) {
                    cursor.setNotificationUri(context.getContentResolver(), uri);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;
        switch (sMatcher.match(uri)) {
            case MATCH_CODE_CACHE:
                count = db.update(CacheOpenHelper.TABLE_NAME, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            default:
                break;
        }

        return count;
    }
}
