package com.novelot.netcache;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class CacheProvider extends ContentProvider {
    public static final String AUTHORITIES = "com.kaolafm.cache.CacheProvider";
    public static final Uri URI = Uri.parse("content://" + AUTHORITIES + "/cache");
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
                return count;
        }

        return count;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        mDb = new CacheDb(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
