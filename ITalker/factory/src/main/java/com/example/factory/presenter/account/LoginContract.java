package com.example.factory.presenter.account;

import android.support.annotation.StringRes;

import com.example.factory.presenter.BaseContract;

/**
 * Created by Administrator on 2017/8/13.
 */

public interface LoginContract {
    interface View extends BaseContract.View<Presenter>{
        //登陆成功
        void loginSuccess();
    }

    interface Presenter extends BaseContract.Presenter{
        // 发起一个login
        void login(String account,String password);
    }
}
