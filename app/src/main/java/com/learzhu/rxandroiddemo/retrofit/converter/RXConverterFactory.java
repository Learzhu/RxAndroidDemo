package com.learzhu.rxandroiddemo.retrofit.converter;


import android.support.annotation.Nullable;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.learzhu.rxandroiddemo.utils.JacksonJsonUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * YZHConverterFactory.java是RxAndroidDemo的解析数据的类。
 *
 * @author Learzhu
 * @version 2.0.0 2017/9/26 10:57
 * @update UserName 2017/9/26 10:57
 * @updateDes
 * @include {@link }
 * @used {@link }
 */
public class RXConverterFactory extends Converter.Factory {
    private int mType;
    private final ObjectMapper mMapper;

    public static RXConverterFactory create(int type) {
        return create(JacksonJsonUtil.getDefaultObjectMapper(), type);
    }

    public static RXConverterFactory create(ObjectMapper mapper, int mType) {
        return new RXConverterFactory(mapper, mType);
    }

    public RXConverterFactory(ObjectMapper mapper, int mType) {
        this.mType = mType;
        this.mMapper = mapper;
    }

    public
    @Nullable
    Converter<ResponseBody, ?> responseBodyConverter(Type type,
                                                     Annotation[] annotations, Retrofit retrofit) {
        JavaType javaType = mMapper.getTypeFactory().constructType(type);
        ObjectReader reader = mMapper.reader(javaType);
        return new RXResponseBodyConverter<>(reader);
    }

    public
    @Nullable
    Converter<?, RequestBody> requestBodyConverter(Type type,
                                                   Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        JavaType javaType = mMapper.getTypeFactory().constructType(type);
        ObjectWriter writer = mMapper.writerWithType(javaType);
        return new RXRequestBodyConverter<>(writer, mType);
    }

}
