package com.novelot.netcache;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * 网络请求类
 * Created by 刘云龙 on 2016/6/1.
 */
class NetRequest {
    private String uri;
    private Callback mCallback;

    public NetRequest(String uri, Callback<CacheRequest> callback) {
        this.uri = uri;
        this.mCallback = callback;
    }

    /**
     * 真正的网络请求
     */
    public void exe() {
        if (TextUtils.isEmpty(uri)) {
            if (mCallback != null) {
                mCallback.onFaiure(new CacheRequestException("uri is null!"));
            }
            return;
        }
        InputStream is = null;
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int code = conn.getResponseCode();
            Log.v("novelot", "uri=" + uri);
            Log.v("novelot", "result code=" + code);
            if (code == 200) {
                Log.v("novelot", "======= host:" + uri + " HTTP 响应头 =======");
                Map<String, List<String>> headerFields = conn.getHeaderFields();
                for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
                    if (entry.getKey() == null) {
                        Log.v("novelot", String.format("======= [%s]", entry.getValue().get(0)));
                    } else {
                        Log.v("novelot", String.format("======= %s:[%s]", entry.getKey(), entry.getValue().get(0)));
                    }
                }
                Log.v("novelot", "======= ======= =======");
                is = conn.getInputStream();
                //
                CacheRequest request = new CacheRequest();
                request.uri = uri;
                request.etag = headerFields.get("ETag").get(0);
                request.lastModified = Utils.turnGMTTime(headerFields.get("Last-Modified").get(0));
                request.updateTime = System.currentTimeMillis();
                request.result = Utils.getStringFromInputStream(is, conn.getContentLength(), conn.getContentEncoding());
                Log.v("novelot", "run result=" + request.toString());
                //
                if (mCallback != null) {
                    mCallback.onSuccess(request);
                }
            } else {
                if (mCallback != null) {
                    mCallback.onFaiure(new CacheRequestException(code));
                }
            }

        } catch (IOException e) {
            if (mCallback != null) {
                mCallback.onFaiure(e);
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetRequest that = (NetRequest) o;

        return uri.equals(that.uri);

    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }
}
