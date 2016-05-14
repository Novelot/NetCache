package com.novelot.netcache;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class CacheProvider extends ContentProvider {
    public static final String AUTHORITIES = "com.kaolafm.cache.CacheProvider";
    public static final Uri URI = Uri.parse("content://" + AUTHORITIES + "/" + CacheDb.TABLE_NAME);
    private static final UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int MATCH_CODE_CACHE = 1;
    private CacheDb mDb;

    static {
        sMatcher.addURI(AUTHORITIES, CacheDb.TABLE_NAME, MATCH_CODE_CACHE);
    }

    public CacheProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDb.getWritableDatabase();
        int count = 0;
        switch (sMatcher.match(uri)) {
            case MATCH_CODE_CACHE:
                count = db.delete(CacheDb.TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
        }

        return count;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        switch (sMatcher.match(uri)) {
            case MATCH_CODE_CACHE:
                return "vnd.android.cursor.dir/vnd." + AUTHORITIES + "." + CacheDb.TABLE_NAME; // one row
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDb.getWritableDatabase();
        switch (sMatcher.match(uri)) {
            case MATCH_CODE_CACHE:
                long insert = db.insertOrThrow(CacheDb.TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
        }

        return uri;
    }

    @Override
    public boolean onCreate() {
        mDb = new CacheDb(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor out = null;
        SQLiteDatabase db = mDb.getWritableDatabase();
        switch (sMatcher.match(uri)) {
            case MATCH_CODE_CACHE:
                out = db.rawQuery("SELECT * FROM " + CacheDb.TABLE_NAME, null);
                Context context = getContext();
                if (null != context) {
                    out.setNotificationUri(context.getContentResolver(), uri);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return out;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
