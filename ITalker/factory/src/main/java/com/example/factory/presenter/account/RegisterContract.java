package com.example.factory.presenter.account;

import android.support.annotation.StringRes;

import com.example.factory.presenter.BaseContract;

/**
 * Created by Administrator on 2017/8/13.
 */

public interface RegisterContract {
    interface View extends BaseContract.View<Presenter>{
        // 提示一个注册成功的方法
        void registerSuccess();
    }

    interface Presenter extends BaseContract.Presenter{
        // 注册方法
        void register(String account,String password,String name);
        // 检查手机号
        boolean checkMobile(String phone);
    }
}
