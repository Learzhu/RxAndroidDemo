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
import android.widget.TextView;

import com.learzhu.rxandroiddemo.R;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * https://github.com/ssseasonnn/RxJava2Demo/blob/master/app/src/main/java/zlc/season/rxjava2demo/demo/ChapterSeven.java
 * 我们把request当做是一种能力, 当成下游处理事件的能力, 下游能处理几个就告诉上游我要几个,
 * 这样只要上游根据下游的处理能力来决定发送多少事件, 就不会造成一窝蜂的发出一堆事件来, 从而导致OOM.
 * 这也就完美的解决之前我们所学到的两种方式的缺陷, 过滤事件会导致事件丢失, 减速又可能导致性能损失.
 * 而这种方式既解决了事件丢失的问题, 又解决了速度的问题, 完美 !
 * Flowable在设计的时候采用了一种新的思路也就是响应式拉取的方式来更好的解决上下游流速不均衡的问题,
 */
public class RxAndroidActivity8 extends AppCompatActivity {
    private static final String TAG = "RxAndroidActivity8";
    private static Subscription mSubscription;

    private TextView mStartTv, mRequestTv;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, RxAndroidActivity8.class);
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
        mStartTv = findViewById(R.id.tv1);
        mRequestTv = findViewById(R.id.tv2);
        mStartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                testFlowableBuffer();
//                testFlowableBufferOOM();
//                testFlowableDROP();
//                testFlowableLATEST();
//                testFlowableLATEST1();
//                testFlowableInterval();
                testFlowableIntervalDROP();
            }
        });
        mRequestTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request(128);
            }
        });
    }

    /**
     * 背压buffer
     */
    private void testFlowableBuffer() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 1000; i++) {
                    Log.d(TAG, "emit " + i);
                    emitter.onNext(i);
                }
            }
        }, BackpressureStrategy.BUFFER) //没有大小限制, 因此可以存放许许多多的事件
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    /**
     * Observable测试的时候内存增长非常迅速, 几秒钟就OOM, 但这里增长速度却比较缓慢,
     * FLowable内部为了实现响应式拉取做了更多的操作, 性能有所丢失也是在所难免
     */
    private void testFlowableBufferOOM() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; ; i++) {
                    Log.d(TAG, "emit " + i);
                    emitter.onNext(i);
                }
            }
        }, BackpressureStrategy.BUFFER) //没有大小限制, 因此可以存放许许多多的事件 还是会OOM
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    private void testFlowableDROP() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; ; i++) {
                    Log.d(TAG, "emit " + i);
                    emitter.onNext(i);
                    Thread.sleep(300);
                }
            }
        }, BackpressureStrategy.DROP)
                //Drop就是直接把存不下的事件丢弃  第一次request的时候, 下游的确收到的是0-127这128个事件
                //第二次request的时候就不确定了, 因为上游一直在发送事件 充满128后就会丢弃后面的
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    /**
     * Latest总是能获取到最后最新的事件
     */
    private void testFlowableLATEST() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; ; i++) {
                    Log.d(TAG, "emit " + i);
                    emitter.onNext(i);
                    Thread.sleep(300);
                }
            }
        }, BackpressureStrategy.LATEST)
                //Drop就是直接把存不下的事件丢弃  第一次request的时候, 下游的确收到的是0-127这128个事件
                //第二次request的时候就不确定了, 因为上游一直在发送事件 充满128后就会丢弃后面的
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    /**
     * Latest总是能获取到最后最新的事件
     * 除去前面128个事件, 与Drop不同, Latest总是能获取到最后最新的事件,
     */
    private void testFlowableLATEST1() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 10000; i++) {
                    Log.d(TAG, "emit " + i);
                    emitter.onNext(i);
                    Thread.sleep(10);
                }
            }
        }, BackpressureStrategy.LATEST)
                //Drop就是直接把存不下的事件丢弃  第一次request的时候, 下游的确收到的是0-127这128个事件
                //第二次request的时候就不确定了, 因为上游一直在发送事件 充满128后就会丢弃后面的
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                        s.request(128); //一开始就处理掉128个事件
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }


    /**
     * interval操作符发送Long型的事件, 从0开始, 每隔指定的时间就把数字加1并发送出来,
     */
    private void testFlowableInterval() {
        Flowable.interval(1, TimeUnit.MICROSECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.d(TAG, "onNext: " + aLong);
                        try {
                            Thread.sleep(1000); //延时1秒
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    /**
     * interval操作符发送Long型的事件, 从0开始, 每隔指定的时间就把数字加1并发送出来,
     */
    private void testFlowableIntervalDROP() {
        Flowable.interval(1, TimeUnit.MICROSECONDS)
                .onBackpressureDrop()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.d(TAG, "onNext: " + aLong);
                        try {
                            Thread.sleep(1000); //延时1秒
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.w(TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    public static void request(long n) {
        mSubscription.request(n); //在外部调用request请求上游
    }
}
