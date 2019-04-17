package com.learzhu.rxandroiddemo.retrofit;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.learzhu.rxandroiddemo.BuildConfig;
import com.learzhu.rxandroiddemo.retrofit.converter.RXConverterFactory;
import com.learzhu.rxandroiddemo.retrofit.converter.RXRequestBodyConverter;
import com.learzhu.rxandroiddemo.utils.AppUtils;
import com.learzhu.rxandroiddemo.utils.LogUtils;
import com.learzhu.rxandroiddemo.utils.VersionUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * ApiRetrofit.java是RxAndroidDemo的类。
 *
 * @author Learzhu
 * @version 2.0.0 2019-04-17 13:45
 * @update Learzhu 2019-04-17 13:45
 * @updateDes
 * @include {@link }
 * @used {@link }
 */
public class ApiRetrofit {
    private static final String TAG = "ApiRetrofit";
    public static final int TIME_OUT_SECONDS_READ = 10;//超时秒数
    public static final int TIME_OUT_SECONDS_WRITE = 60;//超时秒数
    public static final int TIME_OUT_SECONDS_CONNECT = 5;//超时秒数
    public static final String BASE_URL = "www.baidu.com";
    private static ApiRetrofit sInstance = new ApiRetrofit();
    public static int TYPE_JSON = 1;
    public static int TYPE_HTML = 2;
    private Retrofit mRetrofitJSON;
    private Retrofit mRetrofitHTML;

    /**
     * 设置baseUrl
     *
     * @param baseUrl 基础BaseURL
     */
    public synchronized void config(String baseUrl) {
        mRetrofitJSON = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(setupHttpClient())
                //必须加对字符串结果的支持,不然会崩
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(RXConverterFactory.create(TYPE_JSON))
                .build();
        mRetrofitHTML = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(setupHttpClient())
                .addConverterFactory(ScalarsConverterFactory.create())//必须加对字符串结果的支持,不然会崩
                .addConverterFactory(RXConverterFactory.create(TYPE_HTML))
                .build();
    }

    public static Retrofit create() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(TIME_OUT_SECONDS_READ, TimeUnit.SECONDS);
        builder.connectTimeout(9, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }
        return new Retrofit.Builder().baseUrl(BASE_URL)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static void init(String baseUrl) {
        sInstance.config(baseUrl);
    }

    public static <T> T create(Class<T> service) {
        return create(service, TYPE_JSON);
    }

    public static <T> T create(Class<T> service, int type) {
        if (type == TYPE_HTML) {
            return sInstance.createServiceHTML(service);
        }
        return sInstance.createServiceJSON(service);
    }

    public <T> T createServiceJSON(Class<T> service) {
        return mRetrofitJSON.create(service);
    }

    public <T> T createServiceHTML(Class<T> service) {
        return mRetrofitHTML.create(service);
    }

    /**
     * 设置HTTP请求的头部信息
     *
     * @return OkHttpClient Http请求的对象
     */
    private OkHttpClient setupHttpClient() {
//        OkHttpClient builder = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(TIME_OUT_SECONDS_CONNECT, TimeUnit.SECONDS);
        builder.readTimeout(TIME_OUT_SECONDS_READ, TimeUnit.SECONDS);
        builder.writeTimeout(TIME_OUT_SECONDS_WRITE, TimeUnit.SECONDS);
        builder.protocols(Collections.unmodifiableList(Arrays.asList(Protocol.HTTP_1_1, Protocol.HTTP_2)));//启用http2.0协议
        builder.addInterceptor(getHttpLoggingInterceptor());
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();
                //给每个接口统一添加参数 GET请求
//                HttpUrl url = originalHttpUrl.newBuilder().addEncodedQueryParameter("system","android").build();
                HttpUrl url = originalHttpUrl.newBuilder()
                        .addEncodedQueryParameter("clientType", "android")
                        .addEncodedQueryParameter("appVer", VersionUtils.getAppVersionName(AppUtils.getAppContext()))
//                        .addEncodedQueryParameter("clientid", YZHApp.deviceId)
                        .build();
                //为所有的网络请求统一添加header
                Request.Builder rb = original.newBuilder();
//                if (null != YZHApp.sUserData && null != YZHApp.sUserData.getToken()) {
//                    rb = original.newBuilder().header("token", YZHApp.sUserData.getToken()).url(url);
//                    LogUtils.e(TAG, "token: " + YZHApp.sUserData.getToken());
//                } else {
//                    rb = original.newBuilder().header("token", "").url(url);
//                }
                //把请求公有部分添加到header里面,参数里面的也没有去除 权当双管齐下吧
                //毕竟 2019年3月7日 10:13:59 还不清楚 后台到底怎么处理
                rb.header("clientType", "android");
                rb.header("appVer", VersionUtils.getAppVersionName(AppUtils.getAppContext()));
                Request request = rb.build();
                return chain.proceed(request);
            }
        });
        builder.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
//                LogUtils.e("retrofit", "Send -> " + chain.request().url());
                Response response = chain.proceed(chain.request());
                int code = response.code();
                String message = response.message();
                if (code < 200 || code >= 300) {
                    return response.newBuilder()
                            .code(200) //hack: 使response能有机会被Converter,从而抛出正确的异常
                            .body(ResponseBody.create(RXRequestBodyConverter.MEDIA_TYPE_JSON, "code:" + code + ",msg:" + message))
                            .build();
//                    return response.newBuilder()
//                            .code(200) //hack: 使response能有机会被Converter,从而抛出正确的异常
//                            .body(response.body())
//                            .build();
                }
                return response;
            }
        });
        return builder.build();
    }

    //日志记录器
    private HttpLoggingInterceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                //打印retrofit日志
                LogUtils.e(TAG, "retrofitBack = " + message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }
}
