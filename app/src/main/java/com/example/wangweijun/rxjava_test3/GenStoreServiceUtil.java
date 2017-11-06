package com.example.wangweijun.rxjava_test3;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by rookie on 2016/11/15.
 */

public class GenStoreServiceUtil {

    private static final String BASE_URL = ApiService.URL_BASIC_SERVICE_TEST;;

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();


    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.client(httpClient.build()).build();


    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }


}
