package com.novelot.netcache;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by 刘云龙 on 2016/5/31.
 */
class Utils {

    public static long turnGMTTime(String s) {
        return Date.parse(s);
    }

    public static byte[] getByteArrayFromInputStream(InputStream inputStream, int size) throws IOException {
        if (inputStream == null) return null;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(size);
        try {
            byte[] buff = new byte[1024];
            int count;
            while ((count = inputStream.read(buff)) != -1) {
                bytes.write(buff, 0, count);
            }
            return bytes.toByteArray();
        } finally {
            bytes.close();
        }
    }

    public static String getStringFromInputStream(InputStream inputStream, int contentLength, String charSet) {
        Log.v("novelot", "charSet=" + charSet);
        if (inputStream == null) return null;
        byte[] bytes = null;
        try {
            bytes = getByteArrayFromInputStream(inputStream, contentLength);
        } catch (IOException e) {
            return null;
        }

        if (bytes == null) return null;
        return new String(bytes);
    }

    /**
     * 判断是否超过了缓存时间
     *
     * @param updateTime
     * @param cacheTime
     * @return
     */
    public static boolean isOutCacheTime(long updateTime, long cacheTime) {
        boolean result = true;
        if ((System.currentTimeMillis() - updateTime) < cacheTime)
            result = false;
        return result;
    }
}
