package com.learzhu.rxandroiddemo.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TimeUtil.java是液总汇的类。
 *
 * @author Learzhu
 * @version 2.0.0 2019-04-28 17:41
 * @update Learzhu 2019-04-28 17:41
 * @updateDes
 * @include {@link }
 * @used {@link }
 */
public class TimeUtil {
    public static String getNowStrTime() {
        long time = System.currentTimeMillis();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(time));
    }
}
