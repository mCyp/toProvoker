package com.example.factory.presenter.account;

import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.example.factory.R;
import com.example.factory.data.DataSource;
import com.example.factory.data.helper.AccountHelper;
import com.example.factory.model.api.account.LoginModel;
import com.example.factory.model.db.User;
import com.example.factory.persistance.Account;
import com.example.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * 登陆的逻辑实现
 * Created by Administrator on 2017/8/15.
 */

public class LoginPresenter extends BasePresenter<LoginContract.View>
implements LoginContract.Presenter,DataSource.Callback<User>{
    /**
     * 构造函数
     *
     * @param view
     */
    public LoginPresenter(LoginContract.View view) {
        super(view);
    }

    @Override
    public void login(String account, String password) {
        // 调用启动方法 默认启动loading
        start();

        final LoginContract.View view = getView();
        if(TextUtils.isEmpty(account)||TextUtils.isEmpty(password)){
            view.showError(R.string.data_account_login_invalid_parameter);
        }else {
            // 尝试传递pushId
            LoginModel model = new LoginModel(account,password,Account.getPushId());
            AccountHelper.login(model,this);
        }
    }

    @Override
    public void onDataLoaded(User user) {
        // 当网络请求好了 回送一个用户信息
        //  告知界面 注册成功
        final LoginContract.View view = getView();
        if(view == null)
            return;

        //此时可能是从网络发送回来的信息 并不能保证在主线程里
        // 强制执行在主线程中
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.loginSuccess();
            }
        });

    }

    @Override
    public void onDataNotAvailable(@StringRes final int strRes) {
        // 网络请求告知注册失败
        final LoginContract.View view = getView();
        if(view == null)
            return;

        //此时可能是从网络发送回来的信息 并不能保证在主线程里
        // 强制执行在主线程中
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                // 调用注册界面失败显示
                view.showError(strRes);
            }
        });
    }
}
