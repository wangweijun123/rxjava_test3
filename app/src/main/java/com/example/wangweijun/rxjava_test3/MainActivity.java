package com.example.wangweijun.rxjava_test3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.IOException;

import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "XX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void testRxjavaThread(View view) {
        RxjavaApiUtil.testRxjavaThread();
    }


    public void testRxjava(View v) {
        RxjavaApiUtil.testRxjava();
    }

    // Observable
    public void loadDataFromDatabaseAndNetwork(View v) {
        RxjavaApiUtil.loadDataFromDatabaseAndNetwork();
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


    public void testConcatMap(View v) {
       ApiService.rxRetrofitList3();
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


    public void testZipDiffenrentThread2(View v) {
        RxjavaApiUtil.testZipDiffenrentThread2();
    }


    public void testZipArrayMany(View v) {
        RxjavaApiUtil.testZipArrayMany();
    }


    public void testShowUIforMultipartRequest(View v) {
        ApiService.testZip();
    }

    public void testSendEventForever(View v) {
        RxjavaApiUtil.testSendEventForever();
    }

    public void testFlowable(View v) {
        RxjavaApiUtil.testFlowable();
    }


    public void testFlowable2(View v) {
        RxjavaApiUtil.testFlowable2();
    }


    public void testFlowable3(View v) {
        RxjavaApiUtil.testFlowable3();
    }


    public void testFlowable4(View v) {
        RxjavaApiUtil.testFlowable4(getApplicationContext());
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


    public void registerAndLogin2(View v) {
        ApiService.registerAndLogin2(getApplicationContext());
    }

    public void testFilter(View v) {
        RxjavaApiUtil.testFilter();
    }



    @Override
    protected void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }


}
