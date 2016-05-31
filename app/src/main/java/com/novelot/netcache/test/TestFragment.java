package com.novelot.netcache.test;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.novelot.netcache.CacheOpenHelper;
import com.novelot.netcache.CacheProvider;
import com.novelot.netcache.R;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int TOKEN = 0x1;
    private ListView mListView;
    private MyAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(TOKEN, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        mListView = (ListView) view.findViewById(R.id.lv);
        return view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), CacheProvider.URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mAdapter == null) {
            mAdapter = new MyAdapter(data);
        } else {
            Cursor oldCursor = mAdapter.swapCursor(data);
            if (oldCursor == null) {
//                mFullReload = false;
            }
        }

//        Parcelable state = null;
//        if (!mFullReload) {
//            state = mListView.onSaveInstanceState();
//        }
        mListView.setAdapter(mAdapter);
//        if (state != null) {
//            mListView.onRestoreInstanceState(state);
//        }
//        mFullReload = false;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private class MyAdapter extends CursorAdapter {
        public MyAdapter(Cursor c) {
            super(getActivity(), c, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            long lastModified = cursor.getLong(cursor.getColumnIndex(CacheOpenHelper.Columns.LAST_MODIFIED));
            Date date = new Date(lastModified);
//            Calendar.getInstance().setTimeInMillis(lastModified);
            String urlAndResult = cursor.getString(cursor.getColumnIndex(CacheOpenHelper.Columns.URI))
                    + " : " +
                    cursor.getString(cursor.getColumnIndex(CacheOpenHelper.Columns.RESULT))
                    + ":" + date.toGMTString();
            ((TextView) view).setText(urlAndResult);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return new TextView(context);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
