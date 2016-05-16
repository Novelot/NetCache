package com.novelot.netcache.test;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.novelot.netcache.CacheOpenHelper;
import com.novelot.netcache.CacheProvider;
import com.novelot.netcache.R;

public class MainActivity extends FragmentActivity {

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
}
