package com.learzhu.rxandroiddemo.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.learzhu.rxandroiddemo.R;
import com.learzhu.rxandroiddemo.login.request.LoginRequest;
import com.learzhu.rxandroiddemo.login.response.LoginResponse;
import com.learzhu.rxandroiddemo.utils.LogUtils;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private CompositeDisposable mDisposables = new CompositeDisposable();

    private TextView mResultTv;
    private EditText mLoginEt;
    private Button mLoginBtn;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mResultTv = findViewById(R.id.tv_showResult);
        mLoginEt = findViewById(R.id.et_name);
        mLoginBtn = findViewById(R.id.btn_login);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                login();
                retry3login();
//                retryLogin();
            }
        });
    }

    private void login() {
        String name = mLoginEt.getText().toString();
        Observable<LoginRequest> loginResponseObservable = Observable.create(new ObservableOnSubscribe<LoginRequest>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<LoginRequest> emitter) throws Exception {
                LoginRequest request = new LoginRequest();
                request.setName(name);
                if (!emitter.isDisposed()) {
                    emitter.onNext(request);
                }
            }
        });
        DisposableObserver<LoginResponse> loginObserver = new DisposableObserver<LoginResponse>() {
            @Override
            public void onNext(@NonNull LoginResponse loginResponse) {
                LogUtils.e(TAG, "onNext() called with: " + "loginResponse = [" + loginResponse + "]");
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(2000);  //每次发送完事件延时2秒
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //                loginObserver.dispose();
                    String status = loginResponse.getStatus();
                    LogUtils.e(TAG, "onNext() called with: " + "status = [" + status + "]");
//                    mResultTv.setText(status);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        loginResponseObservable.subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.newThread())
                .flatMap(new Function<LoginRequest, ObservableSource<LoginResponse>>() {
                    @Override
                    public ObservableSource<LoginResponse> apply(@NonNull LoginRequest loginRequest) throws Exception {
                        LoginResponse loginResponse = new LoginResponse(loginRequest.getName() + "登录");
                        return Observable.just(loginResponse);
                    }
                }).subscribe(loginObserver);
        mDisposables.add(loginObserver);
    }

    /**
     * 尝试三次登录
     * retryWhen只能控制它所在的上游
     */
    private void retry3login() {
        String name = mLoginEt.getText().toString();
        Observable<LoginRequest> loginResponseObservable = Observable.create(new ObservableOnSubscribe<LoginRequest>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<LoginRequest> emitter) throws Exception {
                LoginRequest request = new LoginRequest();
                request.setName(name);
//                emitter.onError(new Throwable("重试"));
                if (!emitter.isDisposed()) {
                    emitter.onNext(request);
                }
                emitter.onError(new Exception("重试"));
            }
        });
//        Observable<LoginRequest> loginResponseObservable = Observable.defer(new Callable<ObservableSource<LoginRequest>>() {
//            @Override
//            public ObservableSource<LoginResponse> call() throws Exception {
//                return null;
//            }
//        }).flatMap(new Function<LoginRequest, ObservableSource<LoginResponse>>() {
//
//            @Override
//            public ObservableSource<LoginResponse> apply(@NonNull LoginRequest loginRequest) throws Exception {
//                return null;
//            }
//        });
        DisposableObserver<LoginResponse> loginObserver = new DisposableObserver<LoginResponse>() {
            @Override
            public void onNext(@NonNull LoginResponse loginResponse) {
                LogUtils.e(TAG, "onNext() called with: " + "loginResponse = [" + loginResponse + "]");
                try {
                    Thread.sleep(2000);  //每次发送完事件延时2秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String status = loginResponse.getStatus();
                LogUtils.e(TAG, "onNext() called with: " + "status = [" + status + "]");
//                    mResultTv.setText(status);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                LogUtils.e(TAG, "onError() called with: " + "e = [" + e + "]");
            }

            @Override
            public void onComplete() {

            }
        };
        loginResponseObservable.subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.newThread());
        loginResponseObservable.flatMap(new Function<LoginRequest, ObservableSource<LoginResponse>>() {
            @Override
            public ObservableSource<LoginResponse> apply(@NonNull LoginRequest loginRequest) throws Exception {
                LoginResponse loginResponse = new LoginResponse(loginRequest.getName() + "登录");
                return Observable.just(loginResponse);
            }
        }).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            private int mRetryCount = 0;

            @Override
            public ObservableSource<?> apply(@NonNull Observable<Throwable> throwableObservable) throws Exception {
                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull Throwable throwable) throws Exception {
                        LogUtils.e(TAG, ":" + "发生错误=" + throwable + ",重试次数=" + mRetryCount);
                        if (mRetryCount > 2) {
                            return Observable.error(new Throwable("重试"));
                        } else if ("重试".equals(throwable.getMessage())) {
                            mRetryCount++;
                            return Observable.just(new LoginRequest("name test" + mRetryCount));
                            //                            return Observable.error(new Throwable("重试1"));
                        } else {
                            return Observable.error(throwable);
                        }
                    }
                });
            }
        }).subscribe(loginObserver);
//        loginResponseObservable.retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
//            private int mRetryCount = 0;
//
//            @Override
//            public ObservableSource<?> apply(@NonNull Observable<Throwable> throwableObservable) throws Exception {
//                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
//                    @Override
//                    public ObservableSource<?> apply(@NonNull Throwable throwable) throws Exception {
//                        LogUtils.e(TAG, ":" + "发生错误=" + throwable + ",重试次数=" + mRetryCount);
//                        if (mRetryCount > 0) {
//                            return Observable.error(new Throwable("重试"));
//                        } else if ("重试1".equals(throwable.getMessage())) {
//                            mRetryCount++;
//                            return Observable.error(new Throwable("重试1"));
//                        } else {
//                            return Observable.error(throwable);
//                        }
//                    }
//                });
//            }
//        });
        mDisposables.add(loginObserver);
    }

    /**
     * 尝试三次登录
     * retryWhen只能控制它所在的上游
     */
    private void retry3loginError() {
        String name = mLoginEt.getText().toString();
        Observable<LoginRequest> loginResponseObservable = Observable.create(new ObservableOnSubscribe<LoginRequest>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<LoginRequest> emitter) throws Exception {
                LoginRequest request = new LoginRequest();
                request.setName(name);
//                emitter.onError(new Throwable("重试"));
                if (!emitter.isDisposed()) {
                    emitter.onNext(request);
                }
                emitter.onError(new Exception("重试"));
            }
        });
//        Observable<LoginRequest> loginResponseObservable = Observable.defer(new Callable<ObservableSource<LoginRequest>>() {
//            @Override
//            public ObservableSource<LoginResponse> call() throws Exception {
//                return null;
//            }
//        }).flatMap(new Function<LoginRequest, ObservableSource<LoginResponse>>() {
//
//            @Override
//            public ObservableSource<LoginResponse> apply(@NonNull LoginRequest loginRequest) throws Exception {
//                return null;
//            }
//        });
        DisposableObserver<LoginResponse> loginObserver = new DisposableObserver<LoginResponse>() {
            @Override
            public void onNext(@NonNull LoginResponse loginResponse) {
                LogUtils.e(TAG, "onNext() called with: " + "loginResponse = [" + loginResponse + "]");
                try {
                    Thread.sleep(2000);  //每次发送完事件延时2秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String status = loginResponse.getStatus();
                LogUtils.e(TAG, "onNext() called with: " + "status = [" + status + "]");
//                    mResultTv.setText(status);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                LogUtils.e(TAG, "onError() called with: " + "e = [" + e + "]");
            }

            @Override
            public void onComplete() {

            }
        };
        loginResponseObservable.subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.newThread());
        loginResponseObservable.retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            private int mRetryCount = 0;

            @Override
            public ObservableSource<?> apply(@NonNull Observable<Throwable> throwableObservable) throws Exception {
                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull Throwable throwable) throws Exception {
                        LogUtils.e(TAG, ":" + "发生错误=" + throwable + ",重试次数=" + mRetryCount);
                        if (mRetryCount > 2) {
                            return Observable.error(new Throwable("重试"));
                        } else if ("重试".equals(throwable.getMessage())) {
                            mRetryCount++;
                            return Observable.just(new LoginRequest("name test" + mRetryCount));
                            //                            return Observable.error(new Throwable("重试1"));
                        } else {
                            return Observable.error(throwable);
                        }
                    }
                });
            }
        });
        loginResponseObservable.flatMap(new Function<LoginRequest, ObservableSource<LoginResponse>>() {
            @Override
            public ObservableSource<LoginResponse> apply(@NonNull LoginRequest loginRequest) throws Exception {
                LoginResponse loginResponse = new LoginResponse(loginRequest.getName() + "登录");
                return Observable.just(loginResponse);
            }
        }).subscribe(loginObserver);
        mDisposables.add(loginObserver);
    }

    /**
     * 简单地时候就是每次订阅都会创建一个新的 Observable，并且如果没有被订阅，就不会产生新的 Observable。
     * defer是延迟订阅的意思。在订阅的时候，执行ObservableOnScubsribe call方法里面的代码。
     */
    private void retryLogin() {
        String name = mLoginEt.getText().toString();
        Observable<LoginRequest> loginResponseObservable = Observable.defer(new Callable<ObservableSource<LoginRequest>>() {
            @Override
            public ObservableSource<LoginRequest> call() throws Exception {
                return Observable.just(new LoginRequest(name));
            }
        });
        DisposableObserver<LoginResponse> loginObserver = new DisposableObserver<LoginResponse>() {
            @Override
            public void onNext(@NonNull LoginResponse loginResponse) {
                LogUtils.e(TAG, "onNext() called with: " + "loginResponse = [" + loginResponse + "]");
                try {
                    Thread.sleep(2000);  //每次发送完事件延时2秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String status = loginResponse.getStatus();
                LogUtils.e(TAG, "onNext() called with: " + "status = [" + status + "]");
//                    mResultTv.setText(status);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                LogUtils.e(TAG, "onError() called with: " + "e = [" + e + "]");
            }

            @Override
            public void onComplete() {

            }
        };
        loginResponseObservable.subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread());
        loginResponseObservable.retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            private int mRetryCount = 0;

            @Override
            public ObservableSource<?> apply(@NonNull Observable<Throwable> throwableObservable) throws Exception {
                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull Throwable throwable) throws Exception {
                        LogUtils.e(TAG, ":" + "发生错误=" + throwable + ",重试次数=" + mRetryCount);
                        if (mRetryCount > 0) {
                            return Observable.error(new Throwable("重试"));
                        } else if ("重试1".equals(throwable.getMessage())) {
                            mRetryCount++;
                            return Observable.error(new Throwable("重试1"));
                        } else {
                            return Observable.error(throwable);
                        }
                    }
                });
            }
        });
        loginResponseObservable.flatMap(new Function<LoginRequest, ObservableSource<LoginResponse>>() {
            @Override
            public ObservableSource<LoginResponse> apply(@NonNull LoginRequest loginRequest) throws Exception {
                LoginResponse loginResponse = new LoginResponse(loginRequest.getName() + "登录");
                return Observable.just(loginResponse);
            }
        }).subscribe(loginObserver);
        mDisposables.add(loginObserver);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e(TAG, "onDestroy() called with: " + "");
        mDisposables.clear();
    }
}