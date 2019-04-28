package com.learzhu.rxandroiddemo.rxjava;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableOperator;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * RxjavaTest.java是液总汇的类。
 *
 * @author Learzhu
 * @version 2.0.0 2019-04-28 14:38
 * @update Learzhu 2019-04-28 14:38
 * @updateDes
 * @include {@link }
 * @used {@link }
 */
public class RxjavaTest {

    private void testRxjava() {
        Observer<String> observer = new Observer<String>() {
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
        };

        Subscriber<String> subsriber = new Subscriber<String>() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(String s) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        };
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("Hello");
                emitter.onNext("Hi");
                emitter.onNext("Lear");
                emitter.onComplete();
            }
        });

        //依次调用
        Observable<String> observable1 = Observable.just("Hello", "Hi", "Aloha");
        String[] words = {"Hello", "Hi", "Aloha"};
        Observable observable2 = Observable.fromArray(words);

        observable.subscribe(observer);
        observable1.subscribe(observer);

        observable.lift(new ObservableOperator<String, String>() {

            @Override
            public Observer<? super String> apply(Observer<? super String> observer) throws Exception {
                return null;
            }
        });
    }

    public static void main(String args[]) {
    }
}
