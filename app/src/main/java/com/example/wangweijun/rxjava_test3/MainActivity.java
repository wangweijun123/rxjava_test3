package com.example.wangweijun.rxjava_test3;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "XX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void subscribeObserver(View v) {
        RxjavaApiUtil.subscribeObserver();
    }

    public void subscribeConsumer(View v) {
        RxjavaApiUtil.subscribeConsumer();
    }

    public void pointThread(View v) {
        RxjavaApiUtil.pointThread();
    }

    public void pointMultipartThread(View v) {
        RxjavaApiUtil.pointMultipartThread();
    }

    public void pointMultipartThread2(View v) {
        RxjavaApiUtil.pointMultipartThread2();
    }


    /**
     * 后台线程做耗时操作,UI线程显示结果
     */
    CompositeDisposable disposables = new CompositeDisposable();
    boolean flag = true;

    public void testRxjavaByme(View v) {
        flag = !flag;
        RxjavaApiUtil.testRxjavaByme(flag);
    }


    public void testMapConvert(View v) {
        RxjavaApiUtil.testMapConvert();
    }

    public void testflatMap(View v) {
        RxjavaApiUtil.testflatMap();
    }


    public void testZipSameThread(View v) {
        RxjavaApiUtil.testZipSameThread();
    }


    /**
     * @param v
     */
    public void testZipDiffenrentThread(View v) {
        RxjavaApiUtil.testZipDiffenrentThread();
    }

    public void testShowUIforMultipartRequest(View v) {
        ApiService.testZip();
    }

    public void testSendEventForever(View v) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; ; i++) {    //无限循环发事件
                    emitter.onNext(i);
                }
            }
        }).subscribeOn(Schedulers.io())// 上游在IO线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Thread.sleep(2000);
                        Log.d(TAG, "" + integer);
                    }
                });
    }

    public void testFlowable(View v) {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "emit 1");
                emitter.onNext(1);
                Log.d(TAG, "emit 2");
                emitter.onNext(2);
                Log.d(TAG, "emit 3");
                emitter.onNext(3);
                Log.d(TAG, "emit complete");
                emitter.onComplete();
            }
        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    Subscription mSubscription;

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


    public void testFlowable2(View v) {
        Flowable
                .create(new FlowableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                        Log.d(TAG, "current requested: " + emitter.requested());
                    }
                }, BackpressureStrategy.ERROR)
                .subscribe(new Subscriber<Integer>() {
                    Subscription mSubscription;

                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                        s.request(10);// 告诉上游我能处理十个
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


    public void testFlowable3(View v) {
        Flowable
                .create(new FlowableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                        Log.d(TAG, "current requested: " + emitter.requested());
                    }
                }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    Subscription mSubscription;

                    @Override
                    public void onSubscribe(Subscription s) {
                        Log.d(TAG, "onSubscribe");
                        mSubscription = s;
                        s.request(1000);// 告诉上游我能处理十个
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


    public void testFlowable4(View v) {
        Flowable
                .create(new FlowableOnSubscribe<String>() {
                    @Override
                    public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                        try {
                            File dir = Environment.getExternalStorageDirectory();
                            Log.i(TAG, dir.getAbsolutePath());
                            File testFile = new File(dir, "build.xml");
                            FileReader reader = new FileReader(testFile);
                            BufferedReader br = new BufferedReader(reader);

                            String str;

                            while ((str = br.readLine()) != null && !emitter.isCancelled()) {
                                while (emitter.requested() == 0) {
                                    if (emitter.isCancelled()) {
                                        break;
                                    }
                                }
                                emitter.onNext(str);
                            }

                            br.close();
                            reader.close();

                            emitter.onComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                            emitter.onError(e);
                        }
                    }
                }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<String>() {
                    Subscription mSubscription;

                    @Override
                    public void onSubscribe(Subscription s) {
                        mSubscription = s;
                        s.request(1);
                    }

                    @Override
                    public void onNext(String string) {
                        Log.i(TAG, string);
                        try {
                            Thread.sleep(2000);
                            mSubscription.request(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i(TAG, "onError");
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    public void testRxjavaAndRetrofit(View v) {
        ApiService.rxRetrofitList2();
    }


    public void testPostForRxjavaAndRetrofit(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ApiService.doPost();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void testPostForRxjavaAndRetrofit2(View v) {
        ApiService.doPost2();
    }


    public void getRankList(View v) {
        ApiService.getRankList();
    }


    public void testZip(View v) {
        ApiService.testZip();
    }

    public void getFirstPageAndRankList(View v) {
        ApiService.getFirstPageAndRankList();
    }




    @Override
    protected void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }
}
