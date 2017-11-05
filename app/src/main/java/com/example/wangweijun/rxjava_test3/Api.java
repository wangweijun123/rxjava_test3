package com.example.wangweijun.rxjava_test3;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by wangweijun on 2017/11/4.
 */

public interface Api {
    @GET
    Observable<LoginResponse> login(@Body LoginRequest request);

    @GET
    Observable<RegisterResponse> register(@Body RegisterRequest request);


    @GET
    Observable<UserBaseInfoResponse> getUserBaseInfo(@Body UserBaseInfoRequest request);

    @GET
    Observable<UserExtraInfoResponse> getUserExtraInfo(@Body UserExtraInfoRequest request);


    @GET("users/{user}/followers")
    Observable<List<UserFollowerBean>> followers(@Path("user") String usr);

    @GET("/repos/{owner}/{repo}/contributors")
    Observable<List<ApiService.Contributor>> contributors(
            @Path("owner") String owner,
            @Path("repo") String repo);


}
