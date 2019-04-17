package com.learzhu.rxandroiddemo.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.learzhu.rxandroiddemo.BuildConfig;


/**
 * VersionUtils.java是RxAndroidDemo的用于获取版本号的类。
 *
 * @author Learzhu
 * @version 2.0.0 2017/9/26 12:07
 * @update UserName 2017/9/26 12:07
 * @updateDes
 * @include {@link }
 * @used {@link }
 */
public class VersionUtils {

    public static String getVersionCode() {
        return versionToCode(BuildConfig.VERSION_NAME) + "";
    }

    public static int versionToCode(String version) {
        int firstDotIndex = version.indexOf(".");
        int firstIndex = Integer.parseInt(version.substring(0, firstDotIndex));
        int lastDotIndex = version.lastIndexOf(".");
        String middle = version.substring(firstDotIndex + 1, lastDotIndex);
        int middleIndex = Integer.parseInt(middle);
        int lastIndex = Integer.parseInt(version.substring(lastDotIndex + 1));
        return firstIndex * 10000 + middleIndex * 100 + lastIndex;
    }

    /**
     * 获取UA标识中的如：/version20205.P 格式的版本字符串
     *
     * @return
     */
//    public static String getVersionString() {
//        StringBuilder versionBuilder = new StringBuilder();
//        versionBuilder.append("version");
//        versionBuilder.append(getVersionCode());
//        if (Constants.BASE_SERVER.equalsIgnoreCase(DEVELOP_SERVER))
//            versionBuilder.append(".D");
//        else if (Constants.BASE_SERVER.equalsIgnoreCase(TEST_SERVER))
//            versionBuilder.append(".T");
//        else if (Constants.BASE_SERVER.equalsIgnoreCase(PRODUCE_SERVER))
//            versionBuilder.append(".P");
//        return versionBuilder.toString();
//    }

    /**
     * 获取当前app的版本号
     *
     * @param context 上下文对象
     * @return 返回版本号 如果出错则返回-1
     */
    public static int getAppVersionCode(Context context) {
        if (context != null) {
            PackageManager pm = context.getPackageManager();
            if (pm != null) {
                PackageInfo pi;
                try {
                    pi = pm.getPackageInfo(context.getPackageName(), 0);
                    if (pi != null) {
                        return pi.versionCode;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    /**
     * 获取当前版本的版本名称
     *
     * @param context 上下文对象
     * @return 返回版本名称 如果无 则返回null
     */
    public static String getAppVersionName(Context context) {
        if (context != null) {
            PackageManager pm = context.getPackageManager();
            if (pm != null) {
                PackageInfo pi;
                try {
                    pi = pm.getPackageInfo(context.getPackageName(), 0);
                    if (pi != null) {
                        return pi.versionName;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
