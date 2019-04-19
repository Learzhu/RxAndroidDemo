package com.learzhu.rxandroiddemo.rxandroid_test;

import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
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
    private static Subscription mSubscription;

    public static void main(String args[]) {
//        sReadNovel();
        sReadNovel1();
        try {
            Thread.sleep(10000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    private static void sReadNovel1() {
        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                try {
                    String pathName = "E:\\Learzhu\\AndroidFramework\\RxAndroidDemo\\app\\src\\main\\res\\raw\\novel.txt";
//                    FileReader reader = new FileReader("novel.txt");
                    FileReader reader = new FileReader(pathName);
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String string;
                    while ((string = bufferedReader.readLine()) != null && !emitter.isCancelled()) { //此处特别注意是没有被取消才请求
                        while (emitter.requested() == 0) {
                            if (emitter.isCancelled()) {
                                break;
                            }
                        }
                        emitter.onNext(string);
                    }
                    bufferedReader.close();
                    reader.close();
                    emitter.onComplete();
                } catch (IOException e) {
//                    e.printStackTrace();
                    emitter.onError(e);
                }
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        mSubscription = s;
                        s.request(1);
                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println(s);
                        try {
                            Thread.sleep(1000);
                            mSubscription.request(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println(t);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
