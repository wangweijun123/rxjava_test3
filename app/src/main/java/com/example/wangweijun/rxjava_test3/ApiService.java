package com.example.wangweijun.rxjava_test3;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
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

    /** 用户模块的测试地址 */
    public static final String URL_USER_TEST = "http://36.110.161.65";
    public static final String URL_BASIC_SERVICE_TEST = "http://mapi.letvstore.com/";
    public class MyResp {
        String status;
    }

    public class RankResp {
        String status;
    }

    public static class MergeResp {
        String status;
    }

    public static void doPost() throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASIC_SERVICE_TEST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Api service = retrofit.create(Api.class);
        Map<String, String> map = new HashMap<>();
        map.put("isgt" , "1");
        map.put("pagefrom" , "0");
        map.put("packagenames", "com.quicksdk.qnyh.leshi");
        map.put("versioncodes", "18");
        map.put("record", "4,30");
        map.put("pagesize", "30");
        map.put("code", "FOCUS_GAME_NEWINDEX,CREC_CLASSIC_GAME_INDEX_PLUS");
        Call<MyResp> repos = service.doPost(map, getCommonParamsMap());


        Response<MyResp> resp = repos.execute();
        MyResp list = resp.body();
        Log.i(TAG,"list status:"+list.status);
    }


    public static void doPost2() {
        Api service = GenStoreServiceUtil.createService(Api.class);
        Map<String, String> map = new HashMap<>();
        map.put("isgt" , "1");
        map.put("pagefrom" , "0");
        map.put("packagenames", "com.quicksdk.qnyh.leshi");
        map.put("versioncodes", "18");
        map.put("record", "4,30");
        map.put("pagesize", "30");
        map.put("code", "FOCUS_GAME_NEWINDEX,CREC_CLASSIC_GAME_INDEX_PLUS");
        Observable<ApiService.MyResp> myObserve = service.doPost2(map, getCommonParamsMap());

        myObserve
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ApiService.MyResp>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "onSubscribe tid:"+Thread.currentThread().getName());
                    }

                    @Override
                    public void onNext(ApiService.MyResp list) {
                        Log.i(TAG, "onNext tid:"+Thread.currentThread().getName()+", status:"+list.status);
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


    public static void getRankList() {
        Api service = GenStoreServiceUtil.createService(Api.class);
        // pagefrom=1&pagesize=1&code=RANK_HOT";
        Observable<ApiService.RankResp> myObserve = service.doGet("1", "1", "RANK_HOT");
        myObserve
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ApiService.RankResp>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "onSubscribe tid:" + Thread.currentThread().getName());
                    }

                    @Override
                    public void onNext(ApiService.RankResp list) {
                        Log.i(TAG, "onNext tid:" + Thread.currentThread().getName() + ", status:" + list.status);
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


    public static void testZip() {
        Api service = GenStoreServiceUtil.createService(Api.class);
        Map<String, String> map = new HashMap<>();
        map.put("isgt" , "1");
        map.put("pagefrom" , "0");
        map.put("packagenames", "com.quicksdk.qnyh.leshi");
        map.put("versioncodes", "18");
        map.put("record", "4,30");
        map.put("pagesize", "30");
        map.put("code", "FOCUS_GAME_NEWINDEX,CREC_CLASSIC_GAME_INDEX_PLUS");
        // 首页api获取列表数据
        Observable<ApiService.MyResp> observe1 = service.doPost2(map, getCommonParamsMap()).subscribeOn(Schedulers.io());
        // 排行api获取列表数据
        Observable<ApiService.RankResp> observe2 = service.doGet("1", "1", "RANK_HOT").subscribeOn(Schedulers.io());

        Observable.zip(observe1, observe2,
        new BiFunction<ApiService.MyResp, ApiService.RankResp, MergeResp>(){
            @Override
            public MergeResp apply(MyResp myResp, RankResp myResp2) throws Exception {
                Log.i(TAG, "apply : tid:"+Thread.currentThread().getName());
                MergeResp mergeResp = new MergeResp();
                mergeResp.status = "xxxxxx";
                return mergeResp;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MergeResp>() {
                    @Override
                    public void accept(MergeResp mergeResp) throws Exception {
                        Log.i(TAG, "accept :"+mergeResp.status);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        Log.i(TAG, " error ");
                    }
                });

    }

    public static void getFirstPageAndRankList() {
        final Api api = GenStoreServiceUtil.createService(Api.class);
        Map<String, String> map = new HashMap<>();
        map.put("isgt" , "1");
        map.put("pagefrom" , "0");
        map.put("packagenames", "com.quicksdk.qnyh.leshi");
        map.put("versioncodes", "18");
        map.put("record", "4,30");
        map.put("pagesize", "30");
        map.put("code", "FOCUS_GAME_NEWINDEX,CREC_CLASSIC_GAME_INDEX_PLUS");
        api.doPost2(map, getCommonParamsMap())           //发起注册请求
                .subscribeOn(Schedulers.io())               //在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求注册结果
                .doOnNext(new Consumer<ApiService.MyResp>() {
                    @Override
                    public void accept(ApiService.MyResp registerResponse) throws Exception {
                        //先根据注册的响应结果去做一些操作
                        Log.i(TAG, "doOnNext accept tid:"+Thread.currentThread().getName());
                    }
                })
                .observeOn(Schedulers.io())                 //回到IO线程去发起登录请求
                .flatMap(new Function<ApiService.MyResp, ObservableSource<ApiService.RankResp>>() {
                    @Override
                    public ObservableSource<ApiService.RankResp> apply(ApiService.MyResp registerResponse) throws Exception {
                        Log.i(TAG, " apply tid:"+Thread.currentThread().getName());
                        return api.doGet("1", "1", "RANK_HOT");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())  //回到主线程去处理请求登录的结果
                .subscribe(new Consumer<ApiService.RankResp>() {
                    @Override
                    public void accept(ApiService.RankResp response) throws Exception {
                        Log.i(TAG, "subscribe accept tid:"+Thread.currentThread().getName());
                    }
                });
    }

    public static Map<String, String> getCommonParamsMap() {
        Map<String, String> commonParamsMap = new HashMap<String, String>();
        commonParamsMap.put("mac", "");
        commonParamsMap.put("imei", "");
        commonParamsMap.put("storeflag", "ebfzYZIyzcQnvLxVAppEog==");
        // 用户信息
        commonParamsMap.put("productno", "60");
        commonParamsMap.put("productpackageno", "");
        commonParamsMap.put("unitno", "");
        commonParamsMap.put("appversion", "1080");
        commonParamsMap.put("osversion", "");
        commonParamsMap.put("net", "mobile");
        commonParamsMap.put("screensize", "1920*1080");
        commonParamsMap.put("platform", "aphone");
        commonParamsMap.put("osversioncode", "16");
        commonParamsMap.put("channelno", "20"); // 渠道号
        commonParamsMap.put("channelpackageno", "602001"); // 二级渠道号
        commonParamsMap.put("devicemodel", "Le X625");
        commonParamsMap.put("devicebrand", "letv"); // 设备品牌
        commonParamsMap.put("appversioncode", "1080");
        commonParamsMap.put("appversion", "1080");
        commonParamsMap.put("osversion", "6.0");
        commonParamsMap.put("timestamp", "1491669045636");
        commonParamsMap.put("language", "zh");
        return commonParamsMap;
    }
}
