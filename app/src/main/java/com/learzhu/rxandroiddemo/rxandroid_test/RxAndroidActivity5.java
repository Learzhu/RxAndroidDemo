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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * rxjava2.x的Observable是不存在背压的概念的，
 * 首先博主都没有完全理解什么是背压，背压是下游控制上游流速的一种手段。
 * 在rxjava1.x的时代，上游会给下游set一个producer，
 * 下游通过producer向上游请求n个数据，
 * 这样上游就有记录下游请求了多少个数据，
 * 然后下游请求多少个上游就给多少个，这个就是背压。
 * 一般来讲，每个节点都有缓存，比如说缓存的大小是64，
 * 这个时候下游可以一次性向上游request 64个数据。
 * rxjava1.x的有些操作符不支持背压，
 * 也就是说这些操作符不会给下游set一个producer，
 * 也就是上游根本不理会下游的请求，一直向下游丢数据，如果下游的缓存爆了，那么下游就会抛出MissingBackpressureException，也就是背压失效了。
 * 在rxjava2.x时代，上述的背压逻辑全部挪到Flowable里了，所以说Flowable支持背压。而2.x时代的Observable是没有背压的概念的，
 * Observable如果来不及消费会死命的缓存直到OOM，所以rxjava2.x的官方文档里面有讲，
 * 大数据流用Flowable，小数据流用Observable
 */

/**
 * 水缸在Zip内部的实现就是用的队列
 * 将每根水管发出的事件保存起来, 等两个水缸都有事件了之后就分别从水缸中取出一个事件来组合,
 * 当其中一个水缸是空的时候就处于等待的状态.
 * <p>
 * 同步和异步的区别是是否有水缸
 */
public class RxAndroidActivity5 extends AppCompatActivity {
    private static final String TAG = "RxAndroidActivity5";

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, RxAndroidActivity5.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_android);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
//        testZipInfinite1();
//        testZipInfinite2();
        testZipInfinite3();
    }

    /**
     * 管道1不限流发送事件
     */
    private void testZipInfinite1() {
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; ; i++) {
                    //无限发送事件
                    emitter.onNext(i);
                    Log.i(TAG, "subscribe: i: " + i);
                }
            }
        }).subscribeOn(Schedulers.io());
        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("A");
            }
        }).subscribeOn(Schedulers.io());
        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                return integer + s;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.w(TAG, throwable);
            }
        });
    }

    /**
     * 同一个线程无限
     * 当上下游工作在同一个线程中时, 这时候是一个同步的订阅关系,
     * 也就是说上游每发送一个事件必须等到下游接收处理完了以后才能接着发送下一个事件.
     */
    private void testZipInfinite2() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; ; i++) { //无限循环发事件
                    emitter.onNext(i);
                    Log.d(TAG, "subscribe: i: " + i);
                }
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Thread.sleep(2000);
                Log.d(TAG, " " + integer);
            }
        });
    }

    /**
     * 两个线程跑
     * 当上下游工作在不同的线程中时, 这时候是一个异步的订阅关系,
     * 这个时候上游发送数据不需要等待下游接收, 为什么呢,
     * 因为两个线程并不能直接进行通信, 因此上游发送的事件并不能直接到下游里去,
     * 这个时候就需要一个田螺姑娘来帮助它们俩, 这个田螺姑娘就是我们刚才说的水缸 ! 上游把事件发送到水缸里去, 下游从水缸里取出事件来处理,  因此, 当上游发事件的速度太快, 下游取事件的速度太慢, 水缸就会迅速装满, 然后溢出来, 最后就OOM了.
     */
    private void testZipInfinite3() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; ; i++) { //无限循环发事件
                    emitter.onNext(i);
                    Log.d(TAG, "subscribe: i: " + i);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Thread.sleep(2000);
                        Log.d(TAG, " " + integer);
                    }
                });
    }
}
