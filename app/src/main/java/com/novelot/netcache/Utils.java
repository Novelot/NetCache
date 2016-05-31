package com.novelot.netcache;

import java.util.Date;

/**
 * Created by 刘云龙 on 2016/5/31.
 */
class Utils {

    public static long turnGMTTime(String s) {
        return Date.parse(s);
    }
}
