package com.example.wangweijun.rxjava_test3;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testRxjava() throws Exception {
        // ObservableOnSubscribe 事件源
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                // 事件源业务代码
                System.out.println("发送字符串：12 tid:" + Thread.currentThread().getId());
                e.onNext("12");
            }
        }).map(new Function<String, Integer>() {// 重新创建了Observable
            @Override
            public Integer apply(String s) throws Exception {
                System.out.println("字符串转化成int tid:" + Thread.currentThread().getId());
                return Integer.parseInt(s);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer nu) throws Exception {
                // 订阅者业务代码
                System.out.println("接收到.."+nu+ "  tid:" + Thread.currentThread().getId());
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
}