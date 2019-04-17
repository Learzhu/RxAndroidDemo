package com.learzhu.rxandroiddemo.rxandroid_test;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * NovelTest.java是小说连载的测试类。
 *
 * @author Learzhu
 * @version 2.0.0 2019-04-16 16:09
 * @update Learzhu 2019-04-16 16:09
 * @updateDes
 * @include {@link }
 * @used {@link }
 */
public class NovelTest {
    private static final String TAG = "NovelTest";
    private static Disposable sDisposable;

    public static void main(String args[]) {
        sReadNovel();
    }

    private static void sReadNovel() {
        //被观察者 创建连载小说（被观察者）
        Observable<String> novel = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("连载1");
                emitter.onNext("连载2");
                emitter.onNext("连载3");
                emitter.onNext("连载4");
                emitter.onComplete();
            }
        });

        //观察者
        Observer<String> reader = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                sDisposable = d;
                Log.i(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(String s) {
                if ("2".equals(s)) {
                    sDisposable.dispose();
                    return;
                }
                Log.i(TAG, "onNext: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete() ");
            }
        };
        //小说被读者订阅 RxJava为了链式编程 不得不把Observable放前面
        novel.subscribe(reader);
        //产生线程
        novel.subscribeOn(Schedulers.io());
        novel.observeOn(Schedulers.newThread());
    }
}
