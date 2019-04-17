package com.learzhu.rxandroiddemo.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JacksonJsonUtil.java是RX的json转换的类。
 *
 * @author Learzhu
 * @version 2.0.0 2017/9/26 11:07
 * @update UserName 2017/9/26 11:07
 * @updateDes
 * @include {@link }
 * @used {@link }
 */
public class JacksonJsonUtil {
    private static final ObjectMapper sMapper = new ObjectMapper();

    static {
        sMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        sMapper.setVisibilityChecker(sMapper.getSerializationConfig()
                .getDefaultVisibilityChecker());
    }

    public static ObjectMapper getDefaultObjectMapper() {
        return sMapper;
    }

    public static String pojo2json(Object pojo) throws IOException {
        return sMapper.writeValueAsString(pojo);
    }

    public static <T> T json2pojo(String json, Class<T> clazz) throws IOException {
        return sMapper.readValue(json, clazz);
    }

    public static Map<?, ?> json2map(String jsonAsString) throws IOException {
        return sMapper.readValue(jsonAsString, Map.class);
    }

    public static <T> ArrayList<T> json2pojoList(String json, Class<T> clazz) throws IOException {
        JavaType javatype = getCollectionType(ArrayList.class, clazz);
        List<T> list = sMapper.readValue(json, javatype);
        return (ArrayList<T>) list;
    }

    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return sMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

}
