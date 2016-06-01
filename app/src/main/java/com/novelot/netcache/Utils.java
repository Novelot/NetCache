package com.novelot.netcache;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by 刘云龙 on 2016/5/31.
 */
class Utils {

    /**
     * 将字符串转换为GMT毫秒值
     *
     * @param s
     * @return
     */
    public static long turnGMTTime(String s) {
        return Date.parse(s);
    }

    /**
     * 将InputStream转换为byte数组
     *
     * @param inputStream
     * @param size
     * @return
     * @throws IOException
     */
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

    /**
     * 将InputStream转换为String
     *
     * @param inputStream
     * @param contentLength
     * @param charSet
     * @return
     */
    public static String getStringFromInputStream(InputStream inputStream, int contentLength, String charSet) {
        if (inputStream == null) return null;
        byte[] bytes = null;
        try {
            bytes = getByteArrayFromInputStream(inputStream, contentLength);
        } catch (IOException e) {
            return null;
        }

        if (bytes == null) return null;
        String result = null;
        try {
            result = new String(bytes, TextUtils.isEmpty(charSet) ? "utf-8" : charSet);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return result;
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
