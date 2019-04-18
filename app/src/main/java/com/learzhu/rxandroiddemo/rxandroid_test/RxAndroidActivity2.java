package com.learzhu.rxandroiddemo.rxandroid_test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.learzhu.rxandroiddemo.BuildConfig;
import com.learzhu.rxandroiddemo.R;
import com.learzhu.rxandroiddemo.login.response.LoginResponse;
import com.learzhu.rxandroiddemo.login.view.Api;
import com.learzhu.rxandroiddemo.retrofit.ApiRetrofit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RxAndroidActivity2 extends AppCompatActivity {
    private static final String TAG = "RxAndroidActivity2";
    private Context mContext;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, RxAndroidActivity2.class);
        context.startActivity(intent);
    }

    public static void actionStartForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, RxAndroidActivity2.class);
        activity.startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_rx_android);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                RxAndroidActivity3.actionStart(RxAndroidActivity2.this);
//                finish();
                Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri);
                startActivity(intent);
//                finish();
            }
        });
        test();
    }

    /**
     * 测试的方法
     */
    private void test() {
//        Log.d(TAG, "testScheduler: ");
//        testScheduler();
//        Log.d(TAG, "testSchedulerInNewThread: ");
//        testSchedulerInNewThread();
//        Log.d(TAG, "testSchedulerInNewThreadMore: ");
//        testSchedulerInNewThreadMore();
        Log.d(TAG, "testSchedulerInSwitch: ");
        testSchedulerInSwitch();
//        testRetrofit();
    }

    /**
     * 线程调度 默认是同一个线程
     */
    private void testScheduler() {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "Observable thread is : " + Thread.currentThread().getName());
                Log.d(TAG, "emit 1");
                emitter.onNext(1);
            }
        });
        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "Observer thread is :" + Thread.currentThread().getName());
                Log.d(TAG, "onNext: " + integer);
            }
        };
        observable.subscribe(consumer);
    }

    /**
     * 简单的来说, subscribeOn() 指定的是上游发送事件的线程, observeOn() 指定的是下游接收事件的线程.
     * 多次指定上游的线程只有第一次指定的有效, 也就是说多次调用subscribeOn() 只有第一次的有效, 其余的会被忽略.
     * 多次指定下游的线程是可以的, 也就是说每调用一次observeOn() , 下游的线程就会切换一次.
     */
    private void testSchedulerInNewThread() {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "Observable thread is : " + Thread.currentThread().getName());
                Log.d(TAG, "emit 1");
                emitter.onNext(1);
            }
        });
        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "Observer thread is :" + Thread.currentThread().getName());
                Log.d(TAG, "onNext: " + integer);
            }
        };
        //在子线程中做耗时的操作, 然后回到主线程中来操作UI
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    /**
     * subscribeOn() 指定的是上游发送事件的线程, observeOn() 指定的是下游接收事件的线程.
     * 多次指定上游的线程只有第一次指定的有效, 也就是说多次调用subscribeOn() 只有第一次的有效, 其余的会被忽略.
     * 多次指定下游的线程是可以的, 也就是说每调用一次observeOn() , 下游的线程就会切换一次.
     */
    private void testSchedulerInNewThreadMore() {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "Observable thread is : " + Thread.currentThread().getName());
                Log.d(TAG, "emit 1");
                emitter.onNext(1);
            }
        });
        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "Observer thread is :" + Thread.currentThread().getName());
                Log.d(TAG, "onNext: " + integer);
            }
        };
        observable.subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(consumer);
    }

    /**
     * 多个Consumer 多次消费
     * 每调用一次observeOn() 线程便会切换一次
     * Schedulers.io() 代表io操作的线程, 通常用于网络,读写文件等io密集型的操作
     * Schedulers.computation() 代表CPU计算密集型的操作, 例如需要大量计算的操作
     * Schedulers.newThread() 代表一个常规的新线程
     * AndroidSchedulers.mainThread()  代表Android的主线程
     */
    private void testSchedulerInSwitch() {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "Observable thread is : " + Thread.currentThread().getName());
                Log.d(TAG, "emit 1");
                emitter.onNext(1);
            }
        });
        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "Observer thread is :" + Thread.currentThread().getName());
                Log.d(TAG, "onNext: " + integer);
            }
        };
        observable.subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "After observeOn(mainThread), current thread is: " + Thread.currentThread().getName());
                    }
                })
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "After observeOn(io), current thread is : " + Thread.currentThread().getName());
                    }
                })
                .subscribe(consumer);
    }

    private void testRetrofit() {
        RequestBody request = RequestBody.create(MediaType.parse("application/json"), "");
        Api api = ApiRetrofit.create(Api.class);
        api.login(request)
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<LoginResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(LoginResponse value) {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(RxAndroidActivity2.this, "登录成功", Toast.LENGTH_SHORT).show();
                    }
                });
        //那如果有多个Disposable 该怎么办呢, RxJava中已经内置了一个容器CompositeDisposable,
        // 每当我们得到一个Disposable时就调用CompositeDisposable.add()将它添加到容器中,
        // 在退出的时候, 调用CompositeDisposable.clear() 即可切断所有的水管.
        //CompositeDisposable.clear();
    }


    /**
     * 读写数据库也算一个耗时的操作, 因此我们也最好放在IO线程里去进行
     *
     * @return
     */
//    public Observable<List<Record>> readAllRecords() {
//        return Observable.create(new ObservableOnSubscribe<List<Record>>() {
//            @Override
//            public void subscribe(ObservableEmitter<List<Record>> emitter) throws Exception {
//                Cursor cursor = null;
//                try {
//                    cursor = getReadableDatabase().rawQuery("select * from " + TABLE_NAME, new String[]{});
//                    List<Record> result = new ArrayList<>();
//                    while (cursor.moveToNext()) {
//                        result.add(Db.Record.read(cursor));
//                    }
//                    emitter.onNext(result);
//                    emitter.onComplete();
//                } finally {
//                    if (cursor != null) {
//                        cursor.close();
//                    }
//                }
//            }
//        });
//    }

    class Record {
        String name;
    }
}
