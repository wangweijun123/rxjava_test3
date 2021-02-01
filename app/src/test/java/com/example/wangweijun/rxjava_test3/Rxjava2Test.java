package com.example.wangweijun.rxjava_test3;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class Rxjava2Test {
    @Test
    public void testRxjava() throws Exception {
        // ObservableOnSubscribe 事件源
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                // 事件源业务代码
                System.out.println("发送字符串 thread:" + Thread.currentThread().getName());
                e.onNext("12");
            }
        }).map(new Function<String, Integer>() {// 重新创建了Observable
            @Override
            public Integer apply(String s) throws Exception {
                System.out.println("字符串转化成int tid:" + Thread.currentThread().getName());
                return Integer.parseInt(s);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer nu) throws Exception {
                // 订阅者业务代码
                System.out.println("接收到.."+nu+ "  tid:" + Thread.currentThread().getName());
            }
        });

        // Observable.subscribe(xxx)   --内部调用-->   ObservableOnSubscribe.subscribe---->
        // 产生事件,由Emitter发送next下发送事件，订阅者消费事件
    }

    @Test
    public void testRxjava2() throws Exception {
        // ObservableOnSubscribe 事件源
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                // 事件源业务代码
                System.out.println("发送字符串：12");
                e.onNext("12");
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String nu) throws Exception {
                // 订阅者业务代码
                System.out.println("接收到.."+nu);
            }
        });
    }

    @Test
    public void testRxjava3() throws Exception {
        // ObservableOnSubscribe 事件源
        // 背观察者
        Observable<String> stringObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                // 事件源业务代码
                System.out.println("发送字符串：12 " +printThread());
                e.onNext("12");
            }
        });

        //背观察者
        /*Consumer c = new Consumer<String>() {
            @Override
            public void accept(String nu) throws Exception {
                // 订阅者业务代码
                System.out.println("接收到.."+nu);
            }
        };*/

        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                System.out.println("onSubscribe");
            }

            @Override
            public void onNext(@NonNull String s) {
                System.out.println("接收到.."+s + printThread());
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        stringObservable.subscribeOn(Schedulers.io());
        stringObservable.observeOn(Schedulers.io());
        stringObservable.subscribe(observer);

    }


    public String printThread() {
        return "  " +Thread.currentThread().getName() + " " + Thread.currentThread().getId();
    }
}