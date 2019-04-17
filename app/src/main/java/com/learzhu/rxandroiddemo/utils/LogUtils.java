package com.learzhu.rxandroiddemo.utils;

import android.util.Log;

import com.learzhu.rxandroiddemo.BuildConfig;


/**
 * LogUtils.java  是RxAndroidDemo的记录日志的类。
 *
 * @author kk
 * @version 2.0.0 2017/9/29 13:36
 * @update learzhu 2017/10/10 14:57
 * @updateDes 修改使得日志不限长度 Log.i()最长只有4058
 * @include {@link }
 * @used {@link }
 */
public class LogUtils {
    public static boolean DEBUG = false;

    /**
     * 设置日志的最长内容
     */
    private static int LOG_MAXLENGTH = 4058;

    /**
     * 日志 方便隐藏
     *
     * @param tag 标签
     * @param msg 内容
     * @update imgod 修改重复输出问题 2017年10月11日 13:00:16
     */
//    public static void e(String tag, String msg) {
//        if (BuildConfig.LOG_DEBUG || LogUtils.DEBUG) {
//            if (msg.length() > LOG_MAXLENGTH) {
//                int strLength = msg.length();
//                int start = 0;
//                int end = LOG_MAXLENGTH;
//                for (int i = 0; i < 100; i++) {
//                    //剩下的文本还是大于规定长度则继续重复截取并输出
//                    if (strLength > end) {
//                        Log.e(TAG + i, msg.substring(start, end));
//                        start = end;
//                        end = end + LOG_MAXLENGTH;
//                    } else {
//                        Log.e(TAG, msg.substring(start, strLength));
//                        break;
//                    }
//                }
//            } else {
//                Log.e(tag, msg);
//            }
//        }
//    }
    public static void e(String tag, String msg) {  //信息太长,分段打印
        //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，
        //  把4*1024的MAX字节打印长度改为2001字符数
        if (BuildConfig.LOG_DEBUG || LogUtils.DEBUG) {
            int max_str_length = 2001 - tag.length();
            //大于4000时
            while (msg.length() > max_str_length) {
                Log.e(tag, msg.substring(0, max_str_length));
                msg = msg.substring(max_str_length);
            }
            //剩余部分
            Log.e(tag, msg);
        }
    }
}
