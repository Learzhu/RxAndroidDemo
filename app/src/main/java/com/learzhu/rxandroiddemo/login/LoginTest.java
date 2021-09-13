package com.learzhu.rxandroiddemo.login;

import com.learzhu.rxandroiddemo.login.response.LoginResponse;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * LoginTest.java是RxAndroidDemo的类。
 *
 * @author learzhu
 * @version 1.8.2.0 2021/9/13 14:32
 * @update Learzhu 2021/9/13 14:32
 * @updateDes
 * @include {@link }
 * @used {@link }
 * @goto {@link }
 */
public class LoginTest {
    public static void main(String args[]) {
        Observable<LoginResponse> loginResponseObservable = Observable.create(new ObservableOnSubscribe<LoginResponse>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<LoginResponse> emitter) throws Exception {

            }
        });
//        loginResponseObservable.flatMap()
        loginResponseObservable.subscribe(new Consumer<LoginResponse>() {
            @Override
            public void accept(LoginResponse loginResponse) throws Exception {

            }
        });
        DisposableObserver<LoginResponse> loginObserver=new DisposableObserver<LoginResponse>() {
            @Override
            public void onNext(@NonNull LoginResponse loginResponse) {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        loginResponseObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginObserver);
    }
}
