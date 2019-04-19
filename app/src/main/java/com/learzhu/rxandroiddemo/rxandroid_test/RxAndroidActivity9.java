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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
public class RxAndroidActivity9 extends AppCompatActivity {
    private static final String TAG = "RxAndroidActivity9";
    private static Subscription mSubscription;

    private TextView mStartTv, mRequestTv;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, RxAndroidActivity9.class);
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
//                testFlowableERROR();
//                testFlowableRequest();
//                testFlowableRequest1();
//                testFlowableConsumer();
//                testFlowableAsynchronous();
//                testFlowableAsynchronousRequestOpportunity();
                readNovel();
            }
        });
        mRequestTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request(96);
            }
        });
    }

    /**
     * FlowableEmitter是个接口，继承Emitter，Emitter里面就是我们的onNext(),onComplete()和onError()三个方法
     */
    private void testFlowableERROR() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "emit " + 1);
                emitter.onNext(1);
                Log.d(TAG, "emit " + 2);
                emitter.onNext(2);
                Log.d(TAG, "emit " + 3);
                emitter.onNext(3);
                Log.d(TAG, "emit complete");
                emitter.onComplete();
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                        s.request(1);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                        mSubscription.request(1);
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
     * FlowableEmitter是个接口，继承Emitter，Emitter里面就是我们的onNext(),onComplete()和onError()三个方法
     * 上游的requested的确是根据下游的请求来决定的 同步的情况下 下游没有请求 所以为0
     * 当上下游在同一个线程中的时候，在下游调用request(n)就会直接改变上游中的requested的值，
     * 多次调用便会叠加这个值，
     * 而上游每发送一个事件之后便会去减少这个值，当这个值减少至0的时候，继续发送事件便会抛异常了
     */
    private void testFlowableRequest() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "current requested: " + emitter.requested());
            }
        }, BackpressureStrategy.ERROR)
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
     * 多次调用request也没问题，做了加法
     * requested: 110
     */
    private void testFlowableRequest1() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "current requested: " + emitter.requested());
            }
        }, BackpressureStrategy.ERROR)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                        s.request(10);
                        s.request(100);
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
     * 消费 做减法
     * 下游调用request(n) 告诉上游它的处理能力，上游每发送一个next事件之后，requested就减一，
     * 注意是next事件，complete和error事件不会消耗requested，
     * 当减到0时，则代表下游没有处理能力了，这个时候你如果继续发送事件，
     * 当然是MissingBackpressureException啦，
     */
    private void testFlowableConsumer() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "before emit, requested = " + emitter.requested());

                Log.d(TAG, "emit 1");
                emitter.onNext(1);
                Log.d(TAG, "after emit 1, requested = " + emitter.requested());

                Log.d(TAG, "emit 2");
                emitter.onNext(2);
                Log.d(TAG, "after emit 2, requested = " + emitter.requested());

                Log.d(TAG, "emit 3");
                emitter.onNext(3);
                Log.d(TAG, "after emit 3, requested = " + emitter.requested());

                Log.d(TAG, "emit complete");
                emitter.onComplete();

                Log.d(TAG, "after emit complete, requested = " + emitter.requested());
            }
        }, BackpressureStrategy.ERROR)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                        s.request(10);
//                        s.request(2); //处理能力超出限制 MissingBackpressureException
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
     * 异步
     * current requested: 128 处理能力默认128
     * 当上下游工作在不同的线程里时，每一个线程里都有一个requested，而我们调用request（1000）时，
     * 实际上改变的是下游主线程中的requested，而上游中的requested的值是由RxJava内部调用request(n)去设置的，
     * 这个调用会在合适的时候自动触发。
     * 现在我们就能理解为什么没有调用request，上游中的值是128了，
     * 因为下游在一开始就在内部调用了request(128)去设置了上游中的值，因此即使下游没有调用request()，上游也能发送128个事件，这也可以解释之前我们为什么说Flowable中默认的水缸大小是128，其实就是这里设置的。
     */
    private void testFlowableAsynchronous() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "before emit, requested = " + emitter.requested());
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                        s.request(1000);
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
     * 一开始上游的requested的值是128
     * 设置上游requested的值的这个内部调用会在合适的时候自动触发
     * 当下游每消费96个事件便会自动触发内部的request()去设置上游的requested的值啊！
     * 没错，就是这样，而这个新的值就是96。
     * 朋友们可以手动试试请求95个事件，上游是不会继续发送事件的。
     */
    private void testFlowableAsynchronousRequestOpportunity() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "before emit, requested = " + emitter.requested());
                boolean flag;
                for (int i = 0; ; i++) {
                    flag = false;
                    while (emitter.requested() == 0) {
                        if (!flag) {
                            Log.d(TAG, "Oh no! I can't emit value!");
                            flag = true;
                        }
                    }
                    emitter.onNext(i);
                    Log.d(TAG, "emit " + i + " , requested = " + emitter.requested());
                }
            }
        }, BackpressureStrategy.ERROR)
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
     * 阅读小说
     */
    private void readNovel() {
        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                try {
                    String pathName = "E:\\Learzhu\\AndroidFramework\\RxAndroidDemo\\app\\src\\main\\res\\raw\\novel.txt";
//                    FileReader reader = new FileReader("novel.txt");
                    FileReader reader = new FileReader(pathName);
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String string;
                    while ((string = bufferedReader.readLine()) != null && !emitter.isCancelled()) {
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

    public static void request(long n) {
        mSubscription.request(n); //在外部调用request请求上游
    }
}
