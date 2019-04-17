package com.learzhu.rxandroiddemo.login.view;

import com.learzhu.rxandroiddemo.login.response.LoginResponse;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;

/**
 * Api.java是API接口的类。
 *
 * @author Learzhu
 * @version 2.0.0 2019-04-17 13:39
 * @update Learzhu 2019-04-17 13:39
 * @updateDes
 * @include {@link }
 * @used {@link }
 */
public interface Api {
    @GET
    Observable<LoginResponse> login(@Body RequestBody request);

//    @GET
//    Observable<REgisterResponse> register(@Body RegisterRequest request);
}
