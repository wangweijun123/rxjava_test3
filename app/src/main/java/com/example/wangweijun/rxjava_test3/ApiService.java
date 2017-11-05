package com.example.wangweijun.rxjava_test3;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wangweijun on 2017/11/4.
 */

public class ApiService {
    public static Retrofit createRetrofit() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.connectTimeout(9, TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }

        return new Retrofit.Builder().baseUrl("url")
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    CompositeDisposable disposables = new CompositeDisposable();

    // 网络请求
    public void testNetwork(final Context mContext) {
        Api api = createRetrofit().create(Api.class);
        api.login(new LoginRequest())
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Observer<LoginResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
//                        disposables.clear();
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
                        Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void login(final Context mContext) {
        Api api = createRetrofit().create(Api.class);
        api.login(new LoginRequest())
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Consumer<LoginResponse>() {
                    @Override
                    public void accept(LoginResponse loginResponse) throws Exception {
                        Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void register(final Context mContext) {
        Api api = createRetrofit().create(Api.class);
        api.register(new RegisterRequest())
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求结果
                .subscribe(new Consumer<RegisterResponse>() {
                    @Override
                    public void accept(RegisterResponse registerResponse) throws Exception {
                        Toast.makeText(mContext, "注册成功", Toast.LENGTH_SHORT).show();
                        login(mContext);   //注册成功, 调用登录的方法
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(mContext, "注册失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * 切换线程是多么简单
     *
     * @param mContext
     */
    private void loginAndRegister(final Context mContext) {
        final Api api = createRetrofit().create(Api.class);
        api.register(new RegisterRequest())            //发起注册请求
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求注册结果
                .doOnNext(new Consumer<RegisterResponse>() {
                    @Override
                    public void accept(RegisterResponse registerResponse) throws Exception {
                        //先根据注册的响应结果去做一些操作
                    }
                })
                .observeOn(Schedulers.io())                 //回到IO线程去发起登录请求
                .flatMap(new Function<RegisterResponse, ObservableSource<LoginResponse>>() {
                    @Override
                    public ObservableSource<LoginResponse> apply(RegisterResponse registerResponse) throws Exception {
                        return api.login(new LoginRequest());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求登录的结果
                .subscribe(new Consumer<LoginResponse>() {
                    @Override
                    public void accept(LoginResponse response) throws Exception {

                    }
                });
    }


    public void testAccessDatabase(final Context mContext) {

    }

//    public Observable<List<Record>> readAllRecords(final Context mContext) {
//        return Observable.create(new ObservableOnSubscribe<List<Record>>() {
//            @Override
//            public void subscribe(ObservableEmitter<List<Record>> emitter) throws Exception {
//                Cursor cursor = null;
//                try {
//                    cursor = mContext.getReadableDatabase().rawQuery("select * from " + TABLE_NAME, new String[]{});
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
//        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
//    }
private static final String TAG = "XX";

    public static void RxRetrofitList() {
        Api service = GenServiceUtil.createService(Api.class);
        Observable<List<UserFollowerBean>> myObserve = service.followers("lucas");
        myObserve
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<List<UserFollowerBean>, List<UserFollowerBean>>() {
                    @Override
                    public List<UserFollowerBean> apply(List<UserFollowerBean> userFollowerBeen) {
                        for (UserFollowerBean bean : userFollowerBeen) {
                            String name = "";
                            name = bean.getLogin().substring(0, 1).toUpperCase() + bean.getLogin().substring(1, bean.getLogin().length());
                            bean.setLogin(name);
                        }
                        return userFollowerBeen;
                    }
                })
                .map(new Function<List<UserFollowerBean>, List<UserFollowerBean>>() {
                    @Override
                    public List<UserFollowerBean> apply(List<UserFollowerBean> userFollowerBean) {
                        Collections.sort(userFollowerBean, new Comparator<UserFollowerBean>() {
                            @Override
                            public int compare(UserFollowerBean o1, UserFollowerBean o2) {
                                return o1.getLogin().compareTo(o2.getLogin());
                            }
                        });
                        return userFollowerBean;
                    }
                })
                .subscribe(new Observer<List<UserFollowerBean>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(List<UserFollowerBean> value) {
                        Log.i(TAG, "onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.i(TAG, "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete");
                    }
                });
    }


    public static void rxRetrofitList2() {
        Api service = GenServiceUtil.createService(Api.class);
        Observable<List<ApiService.Contributor>> myObserve = service.contributors("square", "retrofit");
        myObserve
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<List<ApiService.Contributor>, List<ApiService.Contributor>>() {
                    @Override
                    public List<ApiService.Contributor> apply(List<ApiService.Contributor> userFollowerBeen) {
                        Log.i(TAG, "map1 apply tid:"+Thread.currentThread().getName());
                        for (ApiService.Contributor bean : userFollowerBeen) {
//                            String name = "";
                        }
                        return userFollowerBeen;
                    }
                })
                .map(new Function<List<ApiService.Contributor>, List<ApiService.Contributor>>() {
                    @Override
                    public List<ApiService.Contributor> apply(List<ApiService.Contributor> userFollowerBean) {
//                        Collections.sort(userFollowerBean, new Comparator<UserFollowerBean>() {
//                            @Override
//                            public int compare(UserFollowerBean o1, UserFollowerBean o2) {
//                                return o1.getLogin().compareTo(o2.getLogin());
//                            }
//                        });
                        Log.i(TAG, "map2 apply tid:"+Thread.currentThread().getName());
                        return userFollowerBean;
                    }
                })
                .subscribe(new Observer<List<ApiService.Contributor>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "onSubscribe tid:"+Thread.currentThread().getName());
                    }

                    @Override
                    public void onNext(List<ApiService.Contributor> list) {
                        Log.i(TAG, "onNext tid:"+Thread.currentThread().getName());
                        for (int i=0; i<list.size(); i++) {
                            Log.i(TAG, list.get(i).toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.i(TAG, "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete");
                    }
                });
    }

    public static final String API_URL = "https://api.github.com";

    public static class Contributor {
        public final String login;
        public final int contributions;

        public Contributor(String login, int contributions) {
            this.login = login;
            this.contributions = contributions;
        }

        @Override
        public String toString() {
            return "login:"+login+", contributions:"+contributions;
        }
    }
}
