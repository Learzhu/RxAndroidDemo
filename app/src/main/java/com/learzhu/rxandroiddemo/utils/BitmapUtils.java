package com.learzhu.rxandroiddemo.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * BitmapUtils.java是RxAndroidDemo的类。
 *
 * @author learzhu
 * @version 1.8.2.0 2021/9/10 17:31
 * @update Learzhu 2021/9/10 17:31
 * @updateDes
 * @include {@link }
 * @used {@link }
 * @goto {@link }
 */
public class BitmapUtils {
    /**
     * 解决androidO Drawable无法转bitmap的问题
     *
     * @param drawable
     * @return
     */
    @NonNull
    public static Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }
}
