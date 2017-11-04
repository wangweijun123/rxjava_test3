package com.example.wangweijun.rxjava_test3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "XX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void subscribeObserver(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 上游发射事件，下游接收事件，默认同一个线程，并且是在当前线程，什么叫当前线程，自己好好理解
                //创建一个上游 Observable：
                Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        Log.i(TAG, "call subscribe tid:" + Thread.currentThread().getId());
                        emitter.onNext(1);// emitter 发射器
                        emitter.onNext(2);
                        emitter.onNext(3);
                        emitter.onComplete();
                    }
                });
                //创建一个下游 Observer
                Observer<Integer> observer = new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {// Disposable 一次性的
                        Log.i(TAG, "subscribe tid:" + Thread.currentThread().getId());
                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.i(TAG, "" + value + ", tid:" + Thread.currentThread().getId());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "error " + ", tid:" + Thread.currentThread().getId());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "complete " + ", tid:" + Thread.currentThread().getId());
                    }
                };
                //建立连接
                observable.subscribe(observer);
            }
        }).start();

    }

    public void subscribeConsumer(View v) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.i(TAG, "emit 1");
                emitter.onNext(1);
                Log.i(TAG, "emit 2");
                emitter.onNext(2);
                Log.i(TAG, "emit 3");
                emitter.onNext(3);
                Log.i(TAG, "emit complete");
                emitter.onComplete();
                Log.i(TAG, "emit 4");
                emitter.onNext(4);
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(TAG, "onNext: " + integer);
            }
        });
    }

    public void swithThread(View v) {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.i(TAG, "Observable thread is : " + Thread.currentThread().getName());
                Log.i(TAG, "emit 1");
                emitter.onNext(1);
            }
        });

        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(TAG, "Observer thread is :" + Thread.currentThread().getName());
                Log.i(TAG, "onNext: " + integer);
            }
        };

        observable.subscribeOn(Schedulers.newThread()) // 指定的是上游发送事件的线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定的是下游接收事件的线程.
                .subscribe(consumer);
    }


    public void setMultipartThread(View v) {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.i(TAG, "Observable thread is : " + Thread.currentThread().getName());
                Log.i(TAG, "emit 1");
                emitter.onNext(1);
            }
        });

        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(TAG, "Observer thread is :" + Thread.currentThread().getName());
                Log.i(TAG, "onNext: " + integer);
            }
        };
        //
        observable.subscribeOn(Schedulers.newThread())// 指定的是上游发送事件的线程
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())// 指定的是下游接收事件的线程.
                .observeOn(Schedulers.io())
                .subscribe(consumer);
    }

    public void setMultipartThread2(View v) {

        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.i(TAG, "Observable thread is : " + Thread.currentThread().getName());
                Log.i(TAG, "emit 1");
                emitter.onNext(1);
            }
        });

        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(TAG, "Observer thread is :" + Thread.currentThread().getName());
                Log.i(TAG, "onNext: " + integer);
            }
        };

        // RxJava内部使用的是线程池来维护这些线程
        //Schedulers.io() 代表io操作的线程, 通常用于网络,读写文件等io密集型的操作
        //Schedulers.computation() 代表CPU计算密集型的操作, 例如需要大量计算的操作
        //Schedulers.newThread() 代表一个常规的新线程
        //AndroidSchedulers.mainThread() 代表Android的主线程
        // 上游与下游多次指定所在线程，上游，第一次指定有效，再次其他忽略， 而下游(各个消费者)可以指定在不同的线程
        observable.subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "After observeOn(mainThread), current thread is: " + Thread.currentThread().getName());
                    }
                })
                .observeOn(Schedulers.io()) // 注释掉这里有意想不到效果
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "After observeOn(io), current thread is : " + Thread.currentThread().getName());
                    }
                })
                .subscribe(consumer);
    }


    /**
     * 后台线程做耗时操作,UI线程显示结果
     */
    CompositeDisposable disposables = new CompositeDisposable();
    boolean flag = true;
    public void testRxjavaByme(View v) {
        final int params = 5;
        Observable<User> observable = Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(ObservableEmitter<User> emitter) throws Exception {
                Log.i(TAG, "observable subscribe tid:" + Thread.currentThread().getId() + " 开始做耗时任务。。。");
                Thread.sleep(2000);
                User user = new User();
                user.id = params;
                emitter.onNext(user);

                if (flag) {
                    emitter.onComplete();
                } else {
                    emitter.onError(new Throwable("error"));
                }
                flag = !flag;
            }
        });

        Observer<User> observer = new Observer<User>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
                // 显示loading 进度条
                Log.i(TAG, "observer  onSubscribe tid:" + Thread.currentThread().getId() + " 显示loading 进度条");
            }

            @Override
            public void onNext(User value) {
                Log.i(TAG, "observer  onNext value:" + value.id + ", tid:" + Thread.currentThread().getId());
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "observer  onError  tid:" + Thread.currentThread().getId()+ ", 任务失败进度条消失");
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "observer  onComplete  tid:" + Thread.currentThread().getId() + ", 任务完成进度条消失");
            }
        };
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    public void testMapConvert(View v) {
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
                Log.i(TAG, "apply 转换 "+ integer);
                return integer.toString();
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.i(TAG, "accept s:" + s);
            }
        });
    }

    public void testflatMap(View v) {
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
                final List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("I am value " + integer);
                }
                return Observable.fromIterable(list).delay(10, TimeUnit.MILLISECONDS);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });
    }


    public void testZipSameThread(View v) {
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "emit 1");
                emitter.onNext(1);
                Log.d(TAG, "emit 2");
                emitter.onNext(2);
                Log.d(TAG, "emit 3");
                emitter.onNext(3);
                Log.d(TAG, "emit 4");
                emitter.onNext(4);
                Log.d(TAG, "emit complete1");
                emitter.onComplete();
            }
        });

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Log.d(TAG, "emit A");
                emitter.onNext("A");
                Log.d(TAG, "emit B");
                emitter.onNext("B");
                Log.d(TAG, "emit C");
                emitter.onNext("C");
                Log.d(TAG, "emit complete2");
                emitter.onComplete();
            }
        });

        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                return integer + s;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
            }

            @Override
            public void onNext(String value) {
                Log.d(TAG, "onNext: " + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        });

    }


    /**
     *
     * @param v
     */
    public void testZipDiffenrentThread(View v) {
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

                Log.d(TAG, "emit 1");
                emitter.onNext(1);



                Log.d(TAG, "emit 2");
                emitter.onNext(2);

                Log.d(TAG, "emit 3");
                emitter.onNext(3);

                Log.d(TAG, "emit 4");
                emitter.onNext(4);


                Log.d(TAG, "emit complete1");
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                Log.d(TAG, "emit A");
                emitter.onNext("A");

                Log.d(TAG, "emit B");
                emitter.onNext("B");

                Log.d(TAG, "emit C");
                emitter.onNext("C");


                Log.d(TAG, "emit complete2");
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                return integer + s;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
            }

            @Override
            public void onNext(String value) {
                Log.d(TAG, "onNext: " + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        });
    }

    public void testShowUIforMultipartRequest(View v) {
        final Api api = ApiService.createRetrofit().create(Api.class);
        Observable<UserBaseInfoResponse> observable1 =
                api.getUserBaseInfo(new UserBaseInfoRequest()).subscribeOn(Schedulers.io());

        Observable<UserExtraInfoResponse> observable2 =
                api.getUserExtraInfo(new UserExtraInfoRequest()).subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2,
                new BiFunction<UserBaseInfoResponse, UserExtraInfoResponse, UserInfo>() {
                    @Override
                    public UserInfo apply(UserBaseInfoResponse baseInfo,
                                          UserExtraInfoResponse extraInfo) throws Exception {
                        return new UserInfo(baseInfo, extraInfo);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserInfo>() {
                    @Override
                    public void accept(UserInfo userInfo) throws Exception {
                        //do something;

                    }
                });
    }

    @Override
    protected void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }
}
