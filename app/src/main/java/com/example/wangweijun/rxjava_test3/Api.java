package com.example.wangweijun.rxjava_test3;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    //  @FieldMap parameters can only be used with form encoding
    @FormUrlEncoded
    @POST("mapi/edit/postrecommend")
    Call<ApiService.MyResp> doPost(@FieldMap Map<String, String> map,
                                   @HeaderMap Map<String, String> headers);

    @FormUrlEncoded
    @POST("mapi/edit/postrecommend")
    Observable<ApiService.MyResp> doPost2(@FieldMap Map<String, String> map,
                                   @HeaderMap Map<String, String> headers);


    @GET("mapi/edit/recommend")
    Observable<ApiService.RankResp> doGet(@Query("pagefrom") String pagefrom,
                       @Query("pagesize") String pagesize,
                       @Query("code") String code);

}
