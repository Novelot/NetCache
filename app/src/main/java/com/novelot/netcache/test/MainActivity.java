package com.novelot.netcache.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.novelot.netcache.CacheManager;
import com.novelot.netcache.CacheOpenHelper;
import com.novelot.netcache.CacheProvider;
import com.novelot.netcache.CacheRequest;
import com.novelot.netcache.Callback;
import com.novelot.netcache.R;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "novelot";
    private int mIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initData();
        setContentView(R.layout.activity_main);
    }

    private void initData() {
        for (int i = 0; i < 10; i++) {
            addItem(null);
        }
    }


    public void addItem(View view) {
        ContentValues values = new ContentValues();
        values.put(CacheOpenHelper.Columns.URL, "Url-" + (mIndex++));
        getContentResolver().insert(CacheProvider.URI, values);
    }

    public void request(View view) {
        final String uri = "http://www.novelot.com/home.html";
        final Callback<String> callback = new Callback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "result = " + s);
            }

            @Override
            public void onFaiure(Exception e) {
                e.toString();
            }
        };

        boolean isCache = true;
        CacheManager.getInstance().init(getApplication()).quest(uri, callback, isCache);
    }


}
