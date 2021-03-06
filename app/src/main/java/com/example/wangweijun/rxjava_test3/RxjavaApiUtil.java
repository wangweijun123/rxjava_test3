package com.example.wangweijun.rxjava_test3;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangweijun1 on 2017/11/6.
 */

public class RxjavaApiUtil {
    private static final String TAG = "RxjavaApiUtil";

    public static void testRxjavaThread(){
        // 背观察者
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                // 被观察者产生事件, 使用emitter
                System.out.println("发送字符串：12 " +printThread());
                e.onNext("12");
            }
        }).subscribeOn(Schedulers.io())// 指定被观察者运行的线程
                .observeOn(AndroidSchedulers.mainThread())// 指定观察者运行的线程
                .subscribe(new Observer<String>() {
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
                });

        // 分开写为何不切换
        /*Observable<String> stringObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                // 事件源业务代码
                System.out.println("发送字符串：12 " + printThread());
                e.onNext("12");
            }
        });
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
        stringObservable.subscribe(observer);*/

    }

    public static String printThread() {
        return "  " +Thread.currentThread().getName() + " " + Thread.currentThread().getId();
    }

    public static void testRxjava() {
        // ObservableOnSubscribe 事件源, 即生产者
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                // 事件源业务代码(产生事件的地方)
                System.out.println("发送字符串：12 tid:" + Thread.currentThread().getId());
                e.onNext("12");
            }
        })
        .observeOn(Schedulers.io())// 指定观察者的运行线程
        .map(new Function<String, String>() {// 转换
            @Override
            public String apply(String s) throws Exception {
                System.out.println(s + "  第一次map tid:" + Thread.currentThread().getId());
                return s;
            }
        })
        .observeOn(Schedulers.io())// 只影响后面的流程(在这个线程执行)
        .map(new Function<String, Integer>() {// 重新创建了Observable
            @Override
            public Integer apply(String s) throws Exception {
                System.out.println("字符串转化成int tid:" + Thread.currentThread().getId());
                return Integer.parseInt(s);
            }
        })
        .subscribeOn(Schedulers.io()) // 指定被观察者运行的线程
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer nu) throws Exception {
                // 订阅者业务代码
                System.out.println("接收到.." + nu + "  tid:" + Thread.currentThread().getId());
            }
        });

        // Observable.subscribe(xxx)   --内部调用-->   ObservableOnSubscribe.subscribe---->
        // 产生事件,由Emitter发送next下发送事件，订阅者消费事件
    }

    public static void simple() {
        // ObservableOnSubscribe 事件源
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                // 事件源业务代码
                e.onNext("xxxxxx");
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                // 订阅者业务代码
            }
        });
    }

        // Observable.subscribe(xxx)   --内部调用-->   ObservableOnSubscribe.subscribe---->
        // 产生事件,由Emitter发送next下发送事件，订阅者消费事件
    // 这一窜的事件，或者说事件流  map 操作，float 操作
    // 读取数据数据库(io)  --> 刷新ui(main)  --> 网络下载db(io)---> 更新数据库  ---> 刷新ui
    public static void loadDataFromDatabaseAndNetwork() {
        Observable.create(new ObservableOnSubscribe<List<User>>() {
            @Override
            public void subscribe(ObservableEmitter<List<User>> emitter) throws Exception {
                checkMainThread();
                Log.i(TAG, "正在读取数据库缓存 tid:"
                        + Thread.currentThread().getId() + ", threadname:"+Thread.currentThread().getName());
                Thread.sleep(3000);
                Log.i(TAG, "读取数据库缓存完毕");
                List<User> list = new ArrayList<User>();
                User user = new User();
                user.id = 1000;
                list.add(user);
                emitter.onNext(list);// onNext中事件对像为null，回调onError, 不为null回调onNext
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<List<User>>() {
                    @Override
                    public void accept(List<User> users) throws Exception {
                        checkMainThread();
                        Log.i(TAG, "数据库缓存返回 accept tid:" + Thread.currentThread().getId()
                                + ", threadname:"+Thread.currentThread().getName());
                        for (User user : users) {
                            Log.i(TAG, user.toString());
                        }
                    }
                }).observeOn(Schedulers.io()) //注意方法名字这里指定的线程
                .flatMap(new Function<List<User>, ObservableSource<List<Record>>>() {
                    @Override
                    public ObservableSource<List<Record>> apply(final List<User> users) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<List<Record>>() {
                            @Override
                            public void subscribe(ObservableEmitter<List<Record>> emitter) throws Exception {
                                checkMainThread();
                                Log.i(TAG, "正在网络加载 tid:" + Thread.currentThread().getId()
                                        + ", threadname:"+Thread.currentThread().getName());
                                Thread.sleep(3000);
                                Log.i(TAG, "网络加载完毕");
                                List<Record> list = new ArrayList<Record>();
                                for (User user : users) {
                                    Record record = new Record();
                                    record.id = user.id;
                                    list.add(record);
                                }
                                emitter.onNext(list);// onNext中事件对像为null，回调onError, 不为null回调onNext
                                emitter.onComplete();
                            }
                        });
                    }
                })//.observeOn(Schedulers.io())// 这里可以不切换线程
                .flatMap(new Function<List<Record>, ObservableSource<List<Pig>>>() {
                    @Override
                    public ObservableSource<List<Pig>> apply(final List<Record> records) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<List<Pig>>() {
                            @Override
                            public void subscribe(ObservableEmitter<List<Pig>> emitter) throws Exception {
                                checkMainThread();
                                Log.i(TAG, "更新数据库缓存 tid:" + Thread.currentThread().getId()
                                        + ", threadname:"+Thread.currentThread().getName());
                                Thread.sleep(3000);
                                Log.i(TAG, "更新数据库缓存完毕");
                                List<Pig> list = new ArrayList<Pig>();
                                for (Record record : records) {
                                    Pig pig = new Pig();
                                    pig.id = record.id;
                                    list.add(pig);
                                }
                                emitter.onNext(list);// onNext中事件对像为null，回调onError, 不为null回调onNext
                                emitter.onComplete();
                            }
                        });
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Pig>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        checkMainThread();
                        Log.i(TAG, "onSubscribe tid:" + Thread.currentThread().getId()
                                + ", threadname:"+Thread.currentThread().getName());


                    }

                    @Override
                    public void onNext(List<Pig> list) {
                        checkMainThread();
                        Log.i(TAG, "onNext tid:" + Thread.currentThread().getId()
                                + ", threadname:"+Thread.currentThread().getName());
                        for (Pig pig : list) {
                            Log.i(TAG, pig.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError tid:" + Thread.currentThread().getId());
                    }

                    @Override
                    public void onComplete() {
                        checkMainThread();
                        Log.i(TAG, "onComplete tid:" + Thread.currentThread().getId());
                    }
                });
    }

    /**
     * 订阅observer，如果没指定上游与下游线程，默认就是当前线程
     */
    public static void subscribeObserver() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 上游发射事件(按顺序发送事件)，下游接收事件(按顺序接受事件)，默认同一个线程，并且是在当前线程，什么叫当前线程，自己好好理解
                //创建一个上游 Observable：
                //
                Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        //事件源业务代码
                        Log.i(TAG, "call subscribe tid:" + Thread.currentThread().getId());
                        Log.i(TAG, "发射事件 1");
                        emitter.onNext(1);// emitter 发射器
                        Log.i(TAG, "发射事件 2");
                        emitter.onNext(2);
                        Log.i(TAG, "发射事件 3");
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
                        //订阅者业务代码
                        Log.i(TAG, "接收到 事件 " + value + ", tid:" + Thread.currentThread().getId());
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

    /**
     * 订阅消费者
     */
    public static void subscribeConsumer() {
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

    /**
     * 指定上下游执行线程
     */
    public static void pointThread() {
        // 事件流 : 从产生事件-->到加工事件---->再到被订阅者接收到的流程
        // 创建一个事件流
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.i(TAG, "Observable thread is : " + Thread.currentThread().getName());
                Log.i(TAG, "emit 1");
                emitter.onNext(1);
            }
        });

        //创建一个订阅者or观察者
        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(TAG, "Observer thread is :" + Thread.currentThread().getName());
                Log.i(TAG, "onNext: " + integer);
            }
        };

        observable.subscribeOn(Schedulers.newThread()) // 指定的是被观察者发送事件的线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定的是观察者接收事件的线程.
                .subscribe(consumer);
    }

    /**
     * 多次指定上下游执行线程
     */
    public static void pointMultipartThread() {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.i(TAG, "Observable thread is : " + Thread.currentThread().getName());
                Log.i(TAG, "emit 1");
                emitter.onNext(1);

                Log.i(TAG, "emit 2");
                emitter.onNext(2);
            }
        });

        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(TAG, "Observer thread is :" + Thread.currentThread().getName());
                Log.i(TAG, "onNext: " + integer);
            }
        };
        // 多次指定线程，后面的无效
        observable.subscribeOn(Schedulers.newThread())// 指定的是上游发送事件的线程
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())// 指定的是下游接收事件的线程.
                .observeOn(Schedulers.io())
                .subscribe(consumer);
    }

    public static void pointMultipartThread2() {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.i(TAG, "Observable thread is : " + Thread.currentThread().getName());
                Log.i(TAG, "emit 1");
                emitter.onNext(1);
            }
        });
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
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.i(TAG, "Observer thread is :" + Thread.currentThread().getName());
                        Log.i(TAG, "onNext: " + integer);
                    }
                });
    }


    public static void testRxjavaByme(final boolean flag) {
        final int params = 5;
        //
        Observable<User> observable = Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(ObservableEmitter<User> emitter) throws Exception {
                Log.i(TAG, "observable subscribe tid:" + Thread.currentThread().getId() + " 开始做耗时任务。。。");
                Thread.sleep(2000);
                User user = new User();
                user.id = params;
                emitter.onNext(user);
                // 被观察者可以控制状态(success or failed)
                if (flag) {
                    emitter.onComplete();
                } else {
                    emitter.onError(new Throwable("error"));
                }
            }
        });
        // 订阅者或者说观察者
        Observer<User> observer = new Observer<User>() {
            @Override
            public void onSubscribe(Disposable d) {
//                disposables.add(d);
                // 显示loading 进度条
                Log.i(TAG, "observer  onSubscribe tid:" + printThread()+ " 显示loading 进度条");
            }

            @Override
            public void onNext(User value) {
                Log.i(TAG, "observer  onNext value:" + value.id + printThread());
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "observer  onError  tid:" + Thread.currentThread().getId() + ", 任务失败进度条消失");
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


    public static void testMapConvert() {
        // 每次链式调用都会产生一个新的observable
        // Observable 事件流
        // ObservableOnSubscribe 事件源
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override// 上游发送 (执行线程都可以指定)
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.i(TAG, "subscribe tid:" + Thread.currentThread().getId());
                Log.i(TAG, "send 1");
                emitter.onNext(1);
                /*Log.i(TAG, "send 2");
                emitter.onNext(2);
                Log.i(TAG, "send 3");
                emitter.onNext(3);*/
            }
        }).subscribeOn(Schedulers.io())
            .map(new Function<Integer, String>() {
                    @Override// 中间转化(执行线程都可以指定)
                    public String apply(Integer integer) throws Exception {
                        Log.i(TAG, "apply 转换 " + integer + ", tid:" + Thread.currentThread().getId());
                        return integer.toString();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override// 下游接收(执行线程都可以指定)
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "accept s:" + s + ", tid:" + Thread.currentThread().getId());
                    }
                });
    }

    public static void testflatMap() {
        // flat map 多个请求 窜性执行的时候，因为下一个请求需要上一个请求的的结果
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "00000 tid:" + Thread.currentThread().getId() + ", name:" + Thread.currentThread().getName());
                emitter.onNext(1);
//                emitter.onNext(2);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())// 指定 被观察者运行的线程
//                .observeOn(Schedulers.newThread()) // 指定观察者所在的线程
                .flatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(final Integer integer) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<String>() {
                            @Override
                            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                                Log.d(TAG, "fffff tid:" + Thread.currentThread().getId() + ", name:" + Thread.currentThread().getName());
                                emitter.onNext(integer.toString() + "xxxx");
                                emitter.onComplete();
                            }
                        });
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, "accept tid:" + Thread.currentThread().getId() + ", name:" + Thread.currentThread().getName());
                        Log.d(TAG, s);
                    }
                });
    }


    public static void testZipSameThread() {
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


    public static void testZipDiffenrentThread() {
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.d(TAG, "emit 1 tid:" + Thread.currentThread().getId());
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
                Log.d(TAG, "emit A tid:" + Thread.currentThread().getId());
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
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(String value) {
                        Log.d(TAG, "onNext: " + value + ", tid:" + Thread.currentThread().getId());
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
     * zip 操作，合并两个事件(可以在不同线程)
     */
    public static void testZipDiffenrentThread2() {
        Observable<Pig> observable1 = Observable.create(new ObservableOnSubscribe<Pig>() {
            @Override
            public void subscribe(ObservableEmitter<Pig> emitter) throws Exception {
                Log.d(TAG, "上游生成pig开始 tid:" + Thread.currentThread().getId());
                Thread.sleep(3000);
                Pig pig = new Pig();
                pig.id = 1000;
                Log.d(TAG, "3秒后完成 pig:" + pig);
                //                throw new Exception("xxxx"); // 只要其中一个Observable抛出异常，就会回掉到Observer.onError中
                emitter.onNext(pig);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable<Record> observable2 = Observable.create(new ObservableOnSubscribe<Record>() {
            @Override
            public void subscribe(ObservableEmitter<Record> emitter) throws Exception {
                Log.d(TAG, "上游生成record 开始 tid:" + Thread.currentThread().getId());

                Thread.sleep(2000);
                Record record = new Record();
                record.id = 1;
                Log.d(TAG, "两秒后完成 record:" + record);
                emitter.onNext(record);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
        /**
         * 合并两个请求并发，一起返回结果，显示界面
         */
        Observable.zip(observable1, observable2, new BiFunction<Pig, Record, User>() {
            @Override
            public User apply(Pig pig, Record record) throws Exception {
                Log.d(TAG, "apply 生成用户 tid:" + Thread.currentThread().getId() + ", pig:" + pig + ", record:" + record);
                User user = new User();
                user.pig = pig;
                user.record = record;
                Log.d(TAG, "apply user:" + user);
                return user;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    long startTime = 0;

                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe tid:" + Thread.currentThread().getId());
                        startTime = System.currentTimeMillis();
                    }

                    @Override
                    public void onNext(User user) {
                        Log.d(TAG, "onNext: user" + user + ", tid:" + Thread.currentThread().getId() + ", spend time:" + (System.currentTimeMillis() - startTime));
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

    public static void testZipArray() {
        Observable<Pig> observable1 = Observable.create(new ObservableOnSubscribe<Pig>() {
            @Override
            public void subscribe(ObservableEmitter<Pig> emitter) throws Exception {
                Log.d(TAG, "上游生成pig开始 tid:" + Thread.currentThread().getId());
                Thread.sleep(3000);
                Pig pig = new Pig();
                pig.id = 1000;
                Log.d(TAG, "3秒后完成 pig:" + pig);
                emitter.onNext(pig);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable<Record> observable2 = Observable.create(new ObservableOnSubscribe<Record>() {
            @Override
            public void subscribe(ObservableEmitter<Record> emitter) throws Exception {
                Log.d(TAG, "上游生成record 开始 tid:" + Thread.currentThread().getId());

                Thread.sleep(2000);
                Record record = new Record();
                record.id = 1;
                Log.d(TAG, "两秒后完成 record:" + record);
                emitter.onNext(record);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }


    public static void testZipArrayMany() {
        Observable<Integer>[] arr = new Observable[10];

        Arrays.fill(arr, Observable.just(1));

        Observable.zip(Arrays.asList(arr), new Function<Object[], Object>() {
            @Override
            public Object apply(Object[] a) throws Exception {
                return Arrays.toString(a);
            }
        }).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object integer) throws Exception {
                Log.d(TAG, "integer : " + integer);
            }
        });
    }


    public static void testShowUIforMultipartRequest() {
        final Api api = ApiService.createRetrofit().create(Api.class);
        Observable<UserBaseInfoResponse> observable1 =
                api.getUserBaseInfo(new UserBaseInfoRequest()).subscribeOn(Schedulers.io());

        Observable<UserExtraInfoResponse> observable2 =
                api.getUserExtraInfo(new UserExtraInfoRequest()).subscribeOn(Schedulers.io());
        // 只有当两种操作成功才会回掉
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

    public static void testSendEventForever() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; ; i++) {    //无限循环发事件(如果下游没有及时处理，发送的事件全部进入水缸，总有会爆掉),
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


    public static void testFlowable() {
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
                        // 如果去掉这句，onNext只会调用一次，也就是在onSubscribe告诉上游(upStream),下游只能处理n个事件
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


    public static void testFlowable2() {
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

    public static void testFlowable3() {
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

    /**
     * 背压 (下游指定能处理上游发送多少个事件，超过个数收不到)
     * @param context
     */
    public static void testFlowable4(final Context context) {
        Flowable
                .create(new FlowableOnSubscribe<String>() {
                    @Override
                    public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                        try {
//                            File dir = Environment.getExternalStorageDirectory();
                            // /storage/emulated/0/Android/data/com.example.wangweijun.rxjava_test3/cache
                            // 注意sdcard的这个目录是不需要动态获取权限的哦
                            File dir =context.getExternalCacheDir();
                            Log.i(TAG, dir.getAbsolutePath());

                            File testFile = new File(dir, "build.xml");
                            if (!testFile.exists()) {
                                testFile.createNewFile();
                            }
                            BufferedWriter writer = new BufferedWriter(new FileWriter(testFile));
                            for (int i=0; i<10; i++) {
                                writer.write("i:"+i + "\n");
                            }

                            writer.close();


                            FileReader reader = new FileReader(testFile);
                            BufferedReader br = new BufferedReader(reader);

                            String str;

                            while ((str = br.readLine()) != null && !emitter.isCancelled()) {
                                while (emitter.requested() == 0) {
                                    if (emitter.isCancelled()) {
                                        break;
                                    }
                                }
                                Log.i(TAG, Thread.currentThread().getName() +" 发射:"+str);
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
                    public void onNext(String str) {
                        Log.i(TAG, Thread.currentThread().getName() +" onNext  "+str);
                        try {
                            Log.i(TAG,"处理开始...");
                            Thread.sleep(2000);
                            Log.i(TAG,"处理完毕");
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
                        Log.i(TAG, "onComplete");
                    }
                });
    }


    public static void testFilter() {
        // ObservableOnSubscribe 事件源
        Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(ObservableEmitter<List<String>> e) throws Exception {
                // 事件源业务代码
                List<String> result = new ArrayList<>();
                result.add("xxxxx");
                result.add("aaaa");
                Log.i(TAG, "onNext s:" + result);
                e.onNext(result);
            }
        }).filter(new Predicate<List<String>>() {
            @Override
            public boolean test(List<String> s) throws Exception {
                Log.i(TAG, "test s:" + s);
                s.remove(1);
                return true;
            }
        }).subscribe(new Consumer<List<String>>() {
            @Override
            public void accept(List<String> s) throws Exception {
                // 订阅者业务代码
                Log.i(TAG, "accept s:" + s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.i(TAG, "throwable :");
            }
        });
    }

    public void testx() {
    }

    public static boolean checkMainThread() {
        boolean re = Thread.currentThread() == Looper.getMainLooper().getThread();
        Log.i(TAG, "checkMainThread :" + re);
        return re;
    }
}
