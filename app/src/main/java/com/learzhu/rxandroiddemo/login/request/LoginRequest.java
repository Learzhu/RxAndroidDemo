package com.learzhu.rxandroiddemo.login.request;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * LoginRequest.java是RxAndroidDemo的类。
 *
 * @author Learzhu
 * @version 2.0.0 2019-04-17 13:58
 * @update Learzhu 2019-04-17 13:58
 * @updateDes
 * @include {@link }
 * @used {@link }
 */
public class LoginRequest extends RequestBody {
    @Override
    public MediaType contentType() {
        return null;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
    }
}
