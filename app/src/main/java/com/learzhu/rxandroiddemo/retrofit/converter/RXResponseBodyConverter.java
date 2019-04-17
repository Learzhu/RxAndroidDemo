package com.learzhu.rxandroiddemo.retrofit.converter;


import com.fasterxml.jackson.databind.ObjectReader;
import com.learzhu.rxandroiddemo.utils.LogUtils;

import java.io.BufferedReader;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * YZHResponseBodyConverter.java是RxAndroidDemo的请求转换的类。
 *
 * @author Learzhu
 * @version 2.0.0 2017/9/26 11:08
 * @update UserName 2017/9/26 11:08
 * @updateDes
 * @include {@link }
 * @used {@link }
 */
public class RXResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final ObjectReader mObjectWriter;
    private static final String TAG = "YZHResponseBodyConverter";

    RXResponseBodyConverter(ObjectReader writer) {
        this.mObjectWriter = writer;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        BufferedReader reader = new BufferedReader(value.charStream());
        try {
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String jsonString = sb.toString();
//            LogUtils.e("retrofit", "Convert response:\n" + jsonString);
            T resp = null;
            try {
                resp = mObjectWriter.readValue(jsonString);
            } catch (IOException e) {
                if (resp instanceof String) {
                    //如果是String类型
                    resp = (T) jsonString;
                }
                LogUtils.e(TAG, "convert: e: " + e);
                e.printStackTrace();
            }
            return resp;
        } finally {
            try {
                reader.close();
            } catch (IOException ignored) {
                LogUtils.e(TAG, "convert:reader close exception ");
            }
        }
    }
}
