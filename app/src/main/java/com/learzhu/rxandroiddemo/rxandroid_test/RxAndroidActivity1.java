package com.learzhu.rxandroiddemo.rxandroid_test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.learzhu.rxandroiddemo.R;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class RxAndroidActivity1 extends AppCompatActivity {
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, RxAndroidActivity1.class);
        context.startActivity(intent);
    }

    private static final String TAG = "RxAndroidActivity1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_android);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        testDispose();
        testConsumer();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /**
     * 测试Dispose切断水管 上游的事件继续 下游
     * 在收到onNext 2这个事件后, 切断了水管, 但是上游仍然发送了3, complete, 4这几个事件,
     * 而且上游并没有因为发送了onComplete而停止. 同时可以看到下游的onSubscribe()方法是最先调用的.
     */
    private void testDispose() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.i(TAG, "subscribe: emitter 1");
                emitter.onNext(1);
                Log.i(TAG, "subscribe: emitter 2");
                emitter.onNext(2);
                Log.i(TAG, "subscribe: emitter 3");
                emitter.onNext(3);
                Log.i(TAG, "subscribe: complete");
                emitter.onComplete();
                Log.i(TAG, "subscribe: emitter 4");
                emitter.onNext(4);
            }
        }).subscribe(new Observer<Integer>() {
            private Disposable mDisposable;
            private int i;

            @Override
            public void onSubscribe(Disposable d) {
                //下游的该方法最先调用
                Log.i(TAG, "onSubscribe:");
                mDisposable = d;
            }

            @Override
            public void onNext(Integer integer) {
                Log.i(TAG, "onNext: " + integer);
                i++;
                if (i == 2) {
                    Log.i(TAG, "dispose");
                    mDisposable.dispose();
                    Log.i(TAG, "isDisposed: " + mDisposable.isDisposed());
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: " + e);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: ");
            }
        });
    }

    /**
     * public final Disposable subscribe() {}
     * public final Disposable subscribe(Consumer<? super T> onNext) {}
     * public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError) {}
     * public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete) {}
     * public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError, Action onComplete, Consumer<? super Disposable> onSubscribe) {}
     * public final void subscribe(Observer<? super T> observer) {}
     * 不带任何参数的subscribe() 表示下游不关心任何事件,你上游尽管发你的数据去吧, 老子可不管你发什么.
     * 带有一个Consumer参数的方法表示下游只关心onNext事件, 其他的事件我假装没看见,
     */
    private void testConsumer() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.i(TAG, "subscribe: emitter 1");
                emitter.onNext(1);
                Log.i(TAG, "subscribe: emitter 2");
                emitter.onNext(2);
                Log.i(TAG, "subscribe: emitter 3");
                emitter.onNext(3);
                Log.i(TAG, "subscribe: complete");
                emitter.onComplete();
                Log.i(TAG, "subscribe: emitter 4");
                emitter.onNext(4);
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "onNext: " + integer);
            }
        });
    }

}
