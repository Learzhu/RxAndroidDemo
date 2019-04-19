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

import org.reactivestreams.Subscription;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * https://github.com/ssseasonnn/RxJava2Demo/blob/master/app/src/main/java/zlc/season/rxjava2demo/demo/ChapterSeven.java
 * 我们把request当做是一种能力, 当成下游处理事件的能力, 下游能处理几个就告诉上游我要几个,
 * 这样只要上游根据下游的处理能力来决定发送多少事件, 就不会造成一窝蜂的发出一堆事件来, 从而导致OOM.
 * 这也就完美的解决之前我们所学到的两种方式的缺陷, 过滤事件会导致事件丢失, 减速又可能导致性能损失.
 * 而这种方式既解决了事件丢失的问题, 又解决了速度的问题, 完美 !
 * Flowable在设计的时候采用了一种新的思路也就是响应式拉取的方式来更好的解决上下游流速不均衡的问题,
 */
public class RxAndroidActivity10 extends AppCompatActivity {
    private static final String TAG = "RxAndroidActivity10";
    private static Subscription mSubscription;

    private TextView mStartTv, mRequestTv;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, RxAndroidActivity10.class);
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
//                testMaybe();
                testPlugin();
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
     * maybe测试
     */
    private void testMaybe() {
        // observer = RxJavaPlugins.onSubscribe(this, observer);
        Maybe.just(1)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "Real onSuccess");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, "Real onError");
                    }
                });
    }

    public static void request(long n) {
        mSubscription.request(n); //在外部调用request请求上游
    }

    private void testPlugin() {
        RxJavaPlugins.setOnMaybeSubscribe(new BiFunction<Maybe, MaybeObserver, MaybeObserver>() {
            @Override
            public MaybeObserver apply(Maybe maybe, MaybeObserver maybeObserver) throws Exception {
                return new WrapDownStreamObserver(maybeObserver); //这个maybeObserver就是我们真正的下游
            }
        });
    }

    class WrapDownStreamObserver<T> implements MaybeObserver<T> {

        private MaybeObserver<T> actual;

        public WrapDownStreamObserver(MaybeObserver<T> actual) {
            this.actual = actual;
        }

        @Override
        public void onSubscribe(Disposable d) {
            actual.onSubscribe(d);
        }

        @Override
        public void onSuccess(T t) {
            Log.d(TAG, "Hooked onSuccess");
            actual.onSuccess(t);
        }

        @Override
        public void onError(Throwable e) {
            Log.d(TAG, "Hooked onError");
            actual.onError(e);
        }

        @Override
        public void onComplete() {
            Log.d(TAG, "Hooked onComplete");
            actual.onComplete();
        }
    }
}
