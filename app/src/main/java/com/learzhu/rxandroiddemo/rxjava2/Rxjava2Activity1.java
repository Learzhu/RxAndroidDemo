package com.learzhu.rxandroiddemo.rxjava2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.learzhu.rxandroiddemo.R;
import com.learzhu.rxandroiddemo.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class Rxjava2Activity1 extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Rxjava2Activity1";
    private TextView mRxOperatorsText;
    private Button mCreateBtn, mMapBtn, mZipBtn, mConcatBtn;

    private Disposable mDisposable;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Rxjava2Activity1.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava21);
        mRxOperatorsText = findViewById(R.id.tv_operators);
        findViewById(R.id.btn_create).setOnClickListener(this);
        findViewById(R.id.btn_map).setOnClickListener(this);
        findViewById(R.id.btn_zip).setOnClickListener(this);
        findViewById(R.id.btn_concat).setOnClickListener(this);
        findViewById(R.id.btn_flat_map).setOnClickListener(this);
        findViewById(R.id.btn_concat_map).setOnClickListener(this);
        findViewById(R.id.btn_distinct).setOnClickListener(this);
        findViewById(R.id.btn_filter).setOnClickListener(this);
        findViewById(R.id.btn_buffer).setOnClickListener(this);
        findViewById(R.id.btn_timer).setOnClickListener(this);
        findViewById(R.id.btn_interval).setOnClickListener(this);
        findViewById(R.id.btn_do_on_next).setOnClickListener(this);
        findViewById(R.id.btn_skip).setOnClickListener(this);
        findViewById(R.id.btn_just).setOnClickListener(this);
        findViewById(R.id.btn_take).setOnClickListener(this);

        findViewById(R.id.btn_single).setOnClickListener(this);
        findViewById(R.id.btn_debounce).setOnClickListener(this);
        findViewById(R.id.btn_defer).setOnClickListener(this);
        findViewById(R.id.btn_last).setOnClickListener(this);
        findViewById(R.id.btn_merge).setOnClickListener(this);
        findViewById(R.id.btn_reduce).setOnClickListener(this);
        findViewById(R.id.btn_scan).setOnClickListener(this);
        findViewById(R.id.btn_window).setOnClickListener(this);

    }

    /**
     * create操作符
     * create 操作符应该是最常见的操作符了，主要用于产生一个 Obserable 被观察者对象，
     * 为了方便大家的认知，以后的教程中统一把被观察者 Observable 称为发射器（上游事件），观察者 Observer 称为接收器（下游事件）。
     */
    private void testOperatorCreate() {
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                mRxOperatorsText.append("Observable emit 1" + "\n");
                Log.e(TAG, "Observable emit 1" + "\n");
                e.onNext(1);
                mRxOperatorsText.append("Observable emit 2" + "\n");
                Log.e(TAG, "Observable emit 2" + "\n");
                e.onNext(2);
                mRxOperatorsText.append("Observable emit 3" + "\n");
                Log.e(TAG, "Observable emit 3" + "\n");
                e.onNext(3);
                e.onComplete();
                mRxOperatorsText.append("Observable emit 4" + "\n");
                Log.e(TAG, "Observable emit 4" + "\n");
                e.onNext(4);
            }
        }).subscribe(new Observer<Integer>() {
            private int i;
            private Disposable mDisposable;

            @Override
            public void onSubscribe(Disposable d) {
                mRxOperatorsText.append("onSubscribe : " + d.isDisposed() + "\n");
                Log.e(TAG, "onSubscribe : " + d.isDisposed() + "\n");
                mDisposable = d;
            }

            @Override
            public void onNext(Integer integer) {
                mRxOperatorsText.append("onNext : value : " + integer + "\n");
                Log.e(TAG, "onNext : value : " + integer + "\n");
                i++;
                if (i == 2) {
                    // 在RxJava 2.x 中，新增的Disposable可以做到切断的操作，让Observer观察者不再接收上游事件
                    mDisposable.dispose();
                    mRxOperatorsText.append("onNext : isDisposable : " + mDisposable.isDisposed() + "\n");
                    Log.e(TAG, "onNext : isDisposable : " + mDisposable.isDisposed() + "\n");
                }
            }

            @Override
            public void onError(Throwable e) {
                mRxOperatorsText.append("onError : value : " + e.getMessage() + "\n");
                Log.e(TAG, "onError : value : " + e.getMessage() + "\n");
            }

            @Override
            public void onComplete() {
                mRxOperatorsText.append("onComplete" + "\n");
                Log.e(TAG, "onComplete" + "\n");
            }
        });
    }

    /**
     * Map 基本算是 RxJava 中一个最简单的操作符了，熟悉 RxJava 1.x 的知道，
     * 它的作用是对发射时间发送的每一个事件应用一个函数，是的每一个事件都按照指定的函数去变化，
     * 而在 2.x 中它的作用几乎一致。
     * map 基本作用就是将一个 Observable 通过某种函数关系，转换为另一种 Observable，
     * 例子中就是把我们的 Integer 数据变成了 String 类型
     */
    private void testOperatorMap() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return "This is result " + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                mRxOperatorsText.append("accept: " + s + "\n");
                Log.e(TAG, "accept: " + s + "\n");
            }
        });
    }

    /**
     * zip 专用于合并事件，该合并不是连接（连接操作符后面会说），而是两两配对，也就意味着，
     * 最终配对出的 Observable 发射事件数目只和少的那个相同。
     * zip 组合事件的过程就是分别从发射器 A 和发射器 B 各取出一个事件来组合，并且一个事件只能被使用一次，
     * 组合的顺序是严格按照事件发送的顺序来进行的
     */
    private void testOperatorZip() {
        Observable.zip(getStringObervable(), getIntegerObservable(), new BiFunction<String, Integer, String>() {
            @Override
            public String apply(String s, Integer integer) throws Exception {
                return s + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String string) throws Exception {
                mRxOperatorsText.append("zip : accept : " + string + "\n");
                Log.e(TAG, "zip : accept : " + string + "\n");
            }
        });
    }

    private Observable<Integer> getIntegerObservable() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                if (!e.isDisposed()) {
                    e.onNext(1);
                    mRxOperatorsText.append("Integer emit : 1 \n");
                    Log.e(TAG, "Integer emit : 1 \n");
                    e.onNext(2);
                    mRxOperatorsText.append("Integer emit : 2 \n");
                    Log.e(TAG, "Integer emit : 2 \n");
                    e.onNext(3);
                    mRxOperatorsText.append("Integer emit : 3 \n");
                    Log.e(TAG, "Integer emit : 3 \n");
                    e.onNext(4);
                    mRxOperatorsText.append("Integer emit : 4 \n");
                    Log.e(TAG, "Integer emit : 4 \n");
                    e.onNext(5);
                    mRxOperatorsText.append("Integer emit : 5 \n");
                    Log.e(TAG, "Integer emit : 5 \n");
                }
            }
        });
    }

    private Observable getStringObervable() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                if (!e.isDisposed()) {
                    e.onNext("A");
                    mRxOperatorsText.append("String emit : A \n");
                    Log.e(TAG, "String emit : A \n");
                    e.onNext("B");
                    mRxOperatorsText.append("String emit : B \n");
                    Log.e(TAG, "String emit : B \n");
                    e.onNext("C");
                    mRxOperatorsText.append("String emit : C \n");
                    Log.e(TAG, "String emit : C \n");
                }
            }
        });
    }

    /**
     * 对于单一的把两个发射器连接成一个发射器，虽然 zip 不能完成，但我们还是可以自力更生，
     * 官方提供的 concat 让我们的问题得到了完美解决
     */
    private void testOperatorConcat() {
        Observable.concat(Observable.just(1, 2, 3, 4), Observable.just(5, 6, 7))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        mRxOperatorsText.append("concat : " + integer + "\n");
                        Log.e(TAG, "concat : " + integer + "\n");
                    }
                });
    }

    /**
     * 它可以把一个发射器 Observable 通过某种方法转换为多个 Observables，
     * 然后再把这些分散的 Observables装进一个单一的发射器 Observable。
     * 但有个需要注意的是，flatMap 并不能保证事件的顺序，
     * 如果需要保证，需要用到我们下面要讲的 ConcatMap。
     */
    private void testOperatorFlapMap() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("I am value " + integer);
                }
                int delayTime = (int) (1 + Math.random() * 10);
                return Observable.fromIterable(list).delay(delayTime, TimeUnit.SECONDS);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG, "flatMap : accept : " + s + "\n");
                        mRxOperatorsText.append("flatMap : accept : " + s + "\n");
                    }
                });
    }

    private void testOperatorConcatMap() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("I am value " + integer);
                }
                int delayTime = (int) (1 + Math.random() * 10);
                return Observable.fromIterable(list).delay(delayTime, TimeUnit.SECONDS);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG, "flatMap : accept : " + s + "\n");
                        mRxOperatorsText.append("flatMap : accept : " + s + "\n");
                    }
                });
    }

    private void resetTextView() {
        mRxOperatorsText.setText("");
    }

    /**
     * 排除重复的
     */
    private void testOperatorDistinct() {
        Observable.just(1, 2, 2, 2, 3, 3, 4, 5).distinct()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        mRxOperatorsText.append("distinct : " + integer + "\n");
                        Log.e(TAG, "distinct : " + integer + "\n");
                    }
                });
    }

    /**
     * 过滤掉不符合我们条件的值
     */
    private void testOperatorFilter() {
        Observable.just(1, 20, 56, -6, 7, 0, 19).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer >= 10;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                mRxOperatorsText.append("filter : " + integer + "\n");
                Log.e(TAG, "filter : " + integer + "\n");
            }
        });
    }

    /**
     * buffer 操作符接受两个参数，buffer(count,skip)，作用是将 Observable 中的数据按 skip (步长)
     * 分成最大不超过 count 的 buffer ，然后生成一个  Observable 。
     * 也许你还不太理解，我们可以通过我们的示例图和示例代码来进一步深化它。
     * 我们 buffer 的第一个参数是 count，代表最大取值，在事件足够的时候，一般都是取 count 个值，
     * 然后每次跳过 skip 个事件
     */
    private void testOperatorBuffer() {
        Observable.just(1, 2, 3, 4, 5).buffer(3, 2)
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        mRxOperatorsText.append("buffer size : " + integers.size() + "\n");
                        Log.e(TAG, "buffer size : " + integers.size() + "\n");
                        mRxOperatorsText.append("buffer value : ");
                        Log.e(TAG, "buffer value : ");
                        for (Integer i : integers) {
                            mRxOperatorsText.append(i + "");
                            Log.e(TAG, i + "");
                        }
                        mRxOperatorsText.append("\n");
                        Log.e(TAG, "\n");
                    }
                });
    }

    /**
     * timer 很有意思，相当于一个定时任务。在 1.x 中它还可以执行间隔逻辑，但在 2.x 中此功能被交给了 interval，下一个会介绍。
     * 但需要注意的是，timer 和 interval 均默认在新线程。
     */
    private void testOperatorTimer() {
        mRxOperatorsText.append("timer start : " + TimeUtil.getNowStrTime() + "\n");
        Log.e(TAG, "timer start : " + TimeUtil.getNowStrTime() + "\n");
        Observable.timer(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        mRxOperatorsText.append("timer :" + aLong + " at " + TimeUtil.getNowStrTime() + "\n");
                        Log.e(TAG, "timer :" + aLong + " at " + TimeUtil.getNowStrTime() + "\n");
                    }
                });
    }

    /**
     * interval 操作符用于间隔时间执行某个操作，
     * 其接受三个参数，分别是第一次发送延迟，间隔时间，时间单位。
     */
    private void testOperatorInterval() {
        mRxOperatorsText.append("interval start : " + TimeUtil.getNowStrTime() + "\n");
        Log.e(TAG, "interval start : " + TimeUtil.getNowStrTime() + "\n");
        mDisposable = Observable.interval(3, 2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        mRxOperatorsText.append("interval :" + aLong + " at " + TimeUtil.getNowStrTime() + "\n");
                        Log.e(TAG, "interval :" + aLong + " at " + TimeUtil.getNowStrTime() + "\n");
                    }
                });
    }

    /**
     * 其实觉得 doOnNext 应该不算一个操作符，但考虑到其常用性，我们还是咬咬牙将它放在了这里。它的作用是让订阅者在接收到数据之前干点有意思的事情。
     * 假如我们在获取到数据之前想先保存一下它，
     */
    private void testOperatorDoOnNext() {
        Observable.just(1, 2, 3, 4).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                mRxOperatorsText.append("doOnNext 保存 " + integer + "成功" + "\n");
                Log.e(TAG, "doOnNext 保存 " + integer + "成功" + "\n");
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                mRxOperatorsText.append("doOnNext :" + integer + "\n");
                Log.e(TAG, "doOnNext :" + integer + "\n");
            }
        });
    }

    /**
     * skip 很有意思，其实作用就和字面意思一样，接受一个 long 型参数 count ，代表跳过 count 个数目开始接收。
     */
    private void testOperatorSkip() {
        Observable.just(1, 2, 3, 4, 5).skip(2)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        mRxOperatorsText.append("skip : " + integer + "\n");
                        Log.e(TAG, "skip : " + integer + "\n");
                    }
                });
    }

    /**
     * take，接受一个 long 型参数 count ，代表至多接收 count 个数据。
     */
    private void testOperatorTake() {
        Flowable.fromArray(1, 2, 3, 4, 5).take(2)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        mRxOperatorsText.append("take : " + integer + "\n");
                        Log.e(TAG, "accept: take : " + integer + "\n");
                    }
                });
    }

    /**
     * just，没什么好说的，其实在前面各种例子都说明了，就是一个简单的发射器依次调用 onNext() 方法。
     */
    private void testOperatorJust() {
        Observable.just("1", "2", "3")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mRxOperatorsText.append("accept : onNext : " + s + "\n");
                        Log.e(TAG, "accept : onNext : " + s + "\n");
                    }
                });
    }

    /**
     * Single 只会接收一个参数，而 SingleObserver 只会调用 onError() 或者 onSuccess()。
     */
    private void testOperatorSingle() {
        Single.just(new Random().nextInt())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        mRxOperatorsText.append("single : onSuccess : " + integer + "\n");
                        Log.e(TAG, "single : onSuccess : " + integer + "\n");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRxOperatorsText.append("single : onError : " + e.getMessage() + "\n");
                        Log.e(TAG, "single : onError : " + e.getMessage() + "\n");
                    }
                });
    }

    /**
     * 去除发送间隔时间小于 500 毫秒的发射事件，所以 1 和 3 被去掉了
     * 去除某些事件间隔不符合规则的
     * 两个相邻数据发射的时间间隔决定了前一个数据是否会被丢弃
     * debounce：去抖
     * 当调用函数N秒后，才会执行函数中动作，若在这N秒内又重复调用该函数则将取消前一次调用，并重新计算执行时间。
     * ***注意单位
     */
    private void testOperatorDebounce() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                // send events with simulated time wait
                emitter.onNext(1); // skip 先收到一个1
                Thread.sleep(400);
                emitter.onNext(2); // deliver 过了400ms收到一个2，小于设定时间500ms，把前一个丢掉,现在只有一个2
                Thread.sleep(505);
                emitter.onNext(3); // skip 过了505ms收到一个3，符合设定时间，保存，现在是2、3
                Thread.sleep(100);
                emitter.onNext(4); // deliver 过了100ms收到一个4，小于设定时间，把前一个丢掉，丢掉3，保存4，现在是2、4
                Thread.sleep(605);
                emitter.onNext(5); // deliver 过了605ms收到一个5，符合设定时间，保存，现在是2、4、5
                Thread.sleep(510);
                emitter.onComplete();
            }
        }).debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        mRxOperatorsText.append("debounce :" + integer + "\n");
                        Log.e(TAG, "debounce :" + integer + "\n");
                    }
                });
    }

    /**
     * 简单地时候就是每次订阅都会创建一个新的 Observable，并且如果没有被订阅，就不会产生新的 Observable。
     * defer是延迟订阅的意思。在订阅的时候，执行ObservableOnScubsribe call方法里面的代码。
     */
    private void testOperatorDefer() {
        Observable<Integer> observable = Observable.defer(new Callable<ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> call() throws Exception {
                return Observable.just(1, 2, 3);
            }
        });
        observable.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                mRxOperatorsText.append("defer : " + integer + "\n");
                Log.e(TAG, "defer : " + integer + "\n");
            }

            @Override
            public void onError(Throwable e) {
                mRxOperatorsText.append("defer : onError : " + e.getMessage() + "\n");
                Log.e(TAG, "defer : onError : " + e.getMessage() + "\n");
            }

            @Override
            public void onComplete() {
                mRxOperatorsText.append("defer : onComplete\n");
                Log.e(TAG, "defer : onComplete\n");
            }
        });
    }

    /**
     * last 操作符仅取出可观察到的最后一个值，或者是满足某些条件的最后一项。
     * 通过Observable只发出最后一个项目（或符合某些条件的最后一个项目）
     * 重点在发出(emit)一词，而不是取出。
     */
    private void testOperatorLast() {
        Observable.just(1, 2, 3).last(4)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        mRxOperatorsText.append("last : " + integer + "\n");
                        Log.e(TAG, "last : " + integer + "\n");
                    }
                });
    }

    /**
     * merge 顾名思义，熟悉版本控制工具的你一定不会不知道 merge 命令，
     * 而在 Rx 操作符中，merge 的作用是把多个 Observable 结合起来，
     * 接受可变参数，也支持迭代器集合。注意它和 concat 的区别在于，
     * 不用等到 发射器 A 发送完所有的事件再进行发射器 B 的发送。
     */
    private void testOperatorMerge() {
        Observable.merge(Observable.just(1, 2), Observable.just(3, 4, 5))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        mRxOperatorsText.append("merge :" + integer + "\n");
                        Log.e(TAG, "accept: merge :" + integer + "\n");
                    }
                });
    }

    /**
     * reduce 操作符每次用一个方法处理一个值，可以有一个 seed 作为初始值。
     */
    private void testOperatorReduce() {
        Observable.just(1, 2, 3)
                .reduce(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        return integer + integer2;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                mRxOperatorsText.append("reduce : " + integer + "\n");
                Log.e(TAG, "accept: reduce : " + integer + "\n");
            }
        });
    }

    /**
     * scan 操作符作用和上面的 reduce 一致，唯一区别是 reduce 是个只追求结果的坏人，
     * 而 scan 会始终如一地把每一个步骤都输出。
     */
    private void testOperatorScan() {
        Observable.just(1, 2, 3)
                .scan(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        return integer + integer2;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                mRxOperatorsText.append("scan " + integer + "\n");
                Log.e(TAG, "accept: scan " + integer + "\n");
            }
        });
    }

    /**
     * 按照实际划分窗口，将数据发送给不同的 Observable
     */
    private void testOperatorWindow() {
        mRxOperatorsText.append("window\n");
        Log.e(TAG, "window\n");
        Observable.interval(1, TimeUnit.SECONDS)//间隔一秒发一次
                .take(15)
                .window(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Observable<Long>>() {
                    @Override
                    public void accept(Observable<Long> longObservable) throws Exception {
                        mRxOperatorsText.append("Sub Divide begin...\n");
                        Log.e(TAG, "Sub Divide begin...\n");
                        longObservable.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) throws Exception {
                                        mRxOperatorsText.append("Next:" + aLong + "\n");
                                        Log.e(TAG, "Next:" + aLong + "\n");
                                    }
                                });
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create:
                resetTextView();
                testOperatorCreate();
                break;
            case R.id.btn_map:
                resetTextView();
                testOperatorMap();
                break;
            case R.id.btn_zip:
                resetTextView();
                testOperatorZip();
                break;
            case R.id.btn_concat:
                resetTextView();
                testOperatorConcat();
                break;
            case R.id.btn_flat_map:
                resetTextView();
                testOperatorFlapMap();
                break;
            case R.id.btn_concat_map:
                resetTextView();
                testOperatorConcatMap();
                break;
            case R.id.btn_distinct:
                resetTextView();
                testOperatorDistinct();
                break;
            case R.id.btn_filter:
                resetTextView();
                testOperatorFilter();
                break;
            case R.id.btn_buffer:
                resetTextView();
                testOperatorBuffer();
                break;
            case R.id.btn_timer:
                resetTextView();
                testOperatorTimer();
                break;
            case R.id.btn_interval:
                resetTextView();
                testOperatorInterval();
                break;
            case R.id.btn_do_on_next:
                resetTextView();
                testOperatorDoOnNext();
                break;
            case R.id.btn_skip:
                resetTextView();
                testOperatorSkip();
                break;
            case R.id.btn_take:
                resetTextView();
                testOperatorTake();
                break;
            case R.id.btn_just:
                resetTextView();
                testOperatorJust();
                break;
            case R.id.btn_single:
                resetTextView();
                testOperatorSingle();
                break;
            case R.id.btn_debounce:
                resetTextView();
                testOperatorDebounce();
                break;
            case R.id.btn_defer:
                resetTextView();
                testOperatorDefer();
                break;
            case R.id.btn_last:
                resetTextView();
                testOperatorLast();
                break;
            case R.id.btn_merge:
                resetTextView();
                testOperatorMerge();
                break;
            case R.id.btn_reduce:
                resetTextView();
                testOperatorReduce();
                break;
            case R.id.btn_scan:
                resetTextView();
                testOperatorScan();
                break;
            case R.id.btn_window:
                resetTextView();
                testOperatorWindow();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}
