package com.learzhu.rxandroiddemo;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.learzhu.rxandroiddemo.data.Data;
import com.learzhu.rxandroiddemo.rxandroid_test.RxAndroidActivity1;
import com.learzhu.rxandroiddemo.rxandroid_test.RxAndroidActivity2;
import com.learzhu.rxandroiddemo.rxandroid_test.RxAndroidActivity3;
import com.learzhu.rxandroiddemo.rxandroid_test.RxAndroidActivity4;
import com.learzhu.rxandroiddemo.rxandroid_test.RxAndroidActivity5;
import com.learzhu.rxandroiddemo.rxandroid_test.RxAndroidActivity6;
import com.learzhu.rxandroiddemo.rxandroid_test.RxAndroidActivity7;
import com.learzhu.rxandroiddemo.rxandroid_test.RxAndroidActivity8;
import com.learzhu.rxandroiddemo.rxandroid_test.RxAndroidActivity9;
import com.learzhu.rxandroiddemo.rxjava2.Rxjava2Activity1;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static java.lang.Thread.sleep;

//import static android.os.SystemClock.sleep;

public class MainActivity<onDestory> extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private Disposable mDisposable;

    private int[] mDrawableRes;

    private List<Drawable> mDrawables;

    private BaseAdapter mBaseAdapter;
    private ArrayAdapter<Drawable> mDrawableArrayAdapter;
    private SimpleAdapter mSimpleAdapter;

    private List<Map<String, Drawable>> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawables = new ArrayList<>();

        mDrawableRes = new int[]{R.mipmap.ic_launcher, R.mipmap.ic_launcher_round,
                R.mipmap.ic_launcher, R.mipmap.ic_launcher_round,
                R.mipmap.ic_launcher, R.mipmap.ic_launcher_round,
                R.mipmap.ic_launcher, R.mipmap.ic_launcher_round,
                R.mipmap.ic_launcher, R.mipmap.ic_launcher_round,
                R.mipmap.ic_launcher, R.mipmap.ic_launcher_round};
//        for (int i = 0; i < 4; i++) {
//            Map<String, Drawable> map = new HashMap<>();
//            map.put("1", mDrawables.get(0));
//            mList.add(map);
//        }
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readNovel();
            }
        });
        mDrawableArrayAdapter = new ArrayAdapter<Drawable>(this, android.R.layout.simple_list_item_1, mDrawables);
        complicatedDoSomething(mDrawableRes);
        mBaseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return mDrawables.size();
            }

            @Override
            public Object getItem(int position) {
                return mDrawables.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View layout = View.inflate(MainActivity.this, R.layout.item_simple_adapter, null);
                ImageView face = (ImageView) layout.findViewById(R.id.face);
                TextView name = (TextView) layout.findViewById(R.id.name);
                TextView mark = (TextView) layout.findViewById(R.id.mark);
                face.setImageDrawable(mDrawables.get(position));
                name.setText("Activity" + (position + 1));
                mark.setText(position + 1 + "");
                return layout;
            }
        };
        ListView listView = findViewById(R.id.lv);
        listView.setAdapter(mBaseAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        RxAndroidActivity1.actionStart(MainActivity.this);
                        break;
                    case 1:
                        RxAndroidActivity2.actionStart(MainActivity.this);
                        break;
                    case 2:
                        RxAndroidActivity3.actionStart(MainActivity.this);
                        break;
                    case 3:
                        RxAndroidActivity4.actionStart(MainActivity.this);
                        break;
                    case 4:
                        RxAndroidActivity5.actionStart(MainActivity.this);
                        break;
                    case 5:
                        RxAndroidActivity6.actionStart(MainActivity.this);
                        break;
                    case 6:
                        RxAndroidActivity7.actionStart(MainActivity.this);
                        break;
                    case 7:
                        RxAndroidActivity8.actionStart(MainActivity.this);
                        break;
                    case 8:
                        RxAndroidActivity9.actionStart(MainActivity.this);
                        break;
                    case 9:
                        Rxjava2Activity1.actionStart(MainActivity.this);
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 最基础的用法
     */
    private void BaseUseage() {
        ////创建观察者或者订阅者
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, "onSubscribe: d " + d);
            }

            @Override
            public void onNext(String s) {
                Log.i(TAG, "onNext:  s " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: e " + e);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: ");
            }
        };

        //创建被观察者
        Observable observable = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {
                emitter.onNext("Hello World!");
            }
        });
        observable.subscribe(observer);
    }

    /**
     * RxJava2.x中的基本用法
     */
    private void usageInRxJava2() {
        Subscriber<Data> subscriber = new Subscriber<Data>() {
            @Override
            public void onSubscribe(Subscription s) {
                Log.i(TAG, "onSubscribe: s " + s);
                s.request(1);
            }

            @Override
            public void onNext(Data data) {
                Log.i(TAG, "onNext: " + data.getId() + data.getName());
            }

            @Override
            public void onError(Throwable t) {
                Log.i(TAG, "onError: " + t.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: ");
            }
        };
        Flowable.create(new FlowableOnSubscribe<Data>() {

            @Override
            public void subscribe(FlowableEmitter<Data> emitter) throws Exception {
                Data data = new Data(1, "+Java");
                Data data1 = new Data(2, "+Android");
                emitter.onNext(data);
                emitter.onNext(data1);
                emitter.onComplete();
            }
        }, BackpressureStrategy.BUFFER).subscribe(subscriber);
    }

    private void useageInConsumer() {
        Data data = new Data(1, "+Java");
        Data data1 = new Data(2, "+Android");
        List<Data> list = new ArrayList<>();
        list.add(data);
        list.add(data1);
        Flowable<List<Data>> flowable = Flowable.just(list);
        Consumer consumer = new Consumer<List<Data>>() {
            @Override
            public void accept(List<Data> datas) throws Exception {
                for (Data data : datas) {
                    Log.i(TAG, "accept: " + data.getId());
                }
            }
        };
        flowable.subscribe(consumer);
    }

    private void useageConsumer() {
        Flowable.just("Hello ,I am China!")
                //替代1.x中的action1,接收一个参数，如果是两个参数action2使用BiCustomer，而且删除了action3-9
                //多个参数用Custom<Object[]>
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "accept: s" + s);
                    }
                });
    }

    private void usageInMap() {
        Data data = new Data(1, "+Java");
        Data data1 = new Data(2, "+Android");
        List<Data> list = new ArrayList<>();
        list.add(data);
        list.add(data1);
        Flowable.just(list).map(new Function<List<Data>, List<Data>>() {
            @Override
            public List<Data> apply(List<Data> datas) throws Exception {
                for (Data data : datas) {
                    if (data.getId() == 2) {
                        data.setName(data.getName() + "--DeMon");
                    }
                }
                return datas;
            }
        }).subscribe(new Consumer<List<Data>>() {
            @Override
            public void accept(List<Data> datas) throws Exception {
                for (Data data : datas) {
                    Log.i(TAG, "accept: onNext:three" + data.getId() + data.getName());
                }
            }
        });
    }

    private void usageInFlatMap() {
        Data data = new Data(1, "+Java");
        Data data1 = new Data(2, "+Android");
        List<Data> list = new ArrayList<>();
        list.add(data);
        list.add(data1);
        Flowable.fromIterable(list).flatMap(new Function<Data, Publisher<?>>() {
            @Override
            public Publisher<?> apply(Data data) throws Exception {
                data.setName(data.getName() + "--DeMon");
                return Flowable.fromArray(data);
            }
        }).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                Data data = (Data) o;
                Log.i(TAG, "accept: " + data.getId() + data.getName());
            }
        });
    }

    private void usageInFilter() {
        Flowable.range(1, 100)//从5开始的第10个数（5-14）
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer % 2 == 0; //得到偶数
                    }
                }).take(3)//只要前三个数据
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, "accept: " + integer);
                    }
                });
    }

    private void usageInSchedulers() {
        Observable.range(1, 9)
                .subscribeOn(Schedulers.io())//IO线程 由subscribeOn()指定 产生线程
                .observeOn(Schedulers.newThread())//新线程 由observeOn()指定 消费线程
                .map(new Function<Integer, Object>() {

                    @Override
                    public Object apply(Integer integer) throws Exception {
                        return integer * 10;
                    }
                }).observeOn(Schedulers.io())//IO线程 由observeOn()指定
                .map(new Function<Object, Object>() {

                    @Override
                    public Object apply(Object o) throws Exception {
                        int a = (int) o;
                        return a / 2;
                    }
                }).observeOn(Schedulers.newThread())
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        Log.i(TAG, "accept: " + o);
                    }
                });
    }

    private void usageInMainThread() {
        Observable.just("one", "two", "three", "four", "five").subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void usageInObserving() {
        Looper backgroundLooper = Looper.myLooper();
        Observable.just("one", "two", "three", "four", "five")
                .observeOn(AndroidSchedulers.from(backgroundLooper))
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    void onRunSchedulerExampleButtonClicked() {
        mCompositeDisposable.add(sampleObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {

                    @Override
                    public void onNext(String s) {
                        Log.i(TAG, "onNext: " + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: e " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                }));
    }

    static Observable<String> sampleObservable() {
        return Observable.defer(new Callable<ObservableSource<? extends String>>() {
            @Override
            public ObservableSource<? extends String> call() throws Exception {
                sleep(5000);
                return Observable.just("one", "two", "three", "four", "five");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }

    /**
     * RxJava的小说阅读对象分开
     */
    private void readNovel() {
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
                mDisposable = d;
                Log.i(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(String s) {
                if ("2".equals(s)) {
                    mDisposable.dispose();
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
    }

    private void readNovelChainScheduling() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("连载1");
                emitter.onNext("连载2");
                emitter.onNext("连载3");
                emitter.onNext("连载4");
                emitter.onComplete();
            }
        })
                //回调在主线程
                .observeOn(AndroidSchedulers.mainThread())
                //执行在io线程
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(String s) {
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
                });
    }

    private void timeDoSomething() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(123);
                sleep(3000);
                emitter.onNext(456);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, "accept: " + integer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                });
    }

    /**
     * 保存图片并显示到列表
     *
     * @param drawableRes
     */
    private void complicatedDoSomething(final int[] drawableRes) {
        Observable.create(new ObservableOnSubscribe<Drawable>() {
            @Override
            public void subscribe(ObservableEmitter<Drawable> emitter) throws Exception {
                for (int i = 0; i < drawableRes.length; i++) {
                    Drawable drawable = getResources().getDrawable(mDrawableRes[i]);
                    if (i == 3) {
                        //第四个图片延时3秒后加载
                        sleep(3000);
                    }
                    if (i == 3) {
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        saveBitmap(bitmap, "test.png", Bitmap.CompressFormat.PNG);
                    }
                    //上传到网络
                    if (i == 1) {
                        updateIcon(drawable);
                    }
                    emitter.onNext(drawable);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Drawable>() {
                    @Override
                    public void accept(Drawable drawable) throws Exception {
                        //回调后在UI界面上展示出来
                        mDrawables.add(drawable);
//                        mDrawableArrayAdapter.notifyDataSetChanged();
                        mBaseAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void updateIcon(Drawable drawable) {
    }

    private void saveBitmap(Bitmap bitmap, String name, Bitmap.CompressFormat format) {
        // 创建一个位于SD卡上的文件
        File file = new File(Environment.getExternalStorageDirectory(), name);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            //将位图输出到指定的文件
            bitmap.compress(format, 100, outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
