package com.example.wangweijun.rxjava_test3;

/**
 * Created by wangweijun on 2017/11/5.
 */

class UserInfo {

    UserBaseInfoResponse baseInfo;
    UserExtraInfoResponse extraInfo;

    public UserInfo(UserBaseInfoResponse baseInfo,
                    UserExtraInfoResponse extraInfo) {
        this.baseInfo = baseInfo;
        this.extraInfo = extraInfo;
    }
}
