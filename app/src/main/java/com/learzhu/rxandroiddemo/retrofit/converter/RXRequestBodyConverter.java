package com.learzhu.rxandroiddemo.retrofit.converter;


import com.fasterxml.jackson.databind.ObjectWriter;
import com.learzhu.rxandroiddemo.retrofit.ApiRetrofit;
import com.learzhu.rxandroiddemo.utils.LogUtils;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * YZHRequestBodyConverter.java是RxAndroidDemo的请求参数的解析类。
 *
 * @author Learzhu
 * @version 2.0.0 2017/9/26 11:08
 * @update UserName 2017/9/26 11:08
 * @updateDes
 * @include {@link }
 * @used {@link }
 */
public class RXRequestBodyConverter<T> implements Converter<T, RequestBody> {

    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=UTF-8");
    public static final MediaType MEDIA_TYPE_HTML = MediaType.parse("text/html; charset=UTF-8");

    private final ObjectWriter mObjectWriter;
    private int mType;

    RXRequestBodyConverter(ObjectWriter writer, int type) {
        mType = type;
        this.mObjectWriter = writer;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        byte[] bytes = mObjectWriter.writeValueAsBytes(value);
        LogUtils.e("retrofit", "Convert request:\n" + new String(bytes, "UTF-8"));
        if (mType == ApiRetrofit.TYPE_HTML) {
            return RequestBody.create(MEDIA_TYPE_HTML, value.toString().getBytes());
        }
        return RequestBody.create(MEDIA_TYPE_JSON, bytes);
    }
}
