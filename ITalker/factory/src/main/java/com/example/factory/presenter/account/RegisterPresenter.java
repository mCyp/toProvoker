package com.example.factory.presenter.account;

import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.example.common.Common;
import com.example.factory.R;
import com.example.factory.data.DataSource;
import com.example.factory.data.helper.AccountHelper;
import com.example.factory.model.api.account.RegisterModel;
import com.example.factory.model.db.User;
import com.example.factory.persistance.Account;
import com.example.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/8/13.
 */

public class RegisterPresenter extends BasePresenter<RegisterContract.View>
        implements RegisterContract.Presenter,DataSource.Callback<User> {


    /**
     * 构造函数
     *
     * @param view
     */
    public RegisterPresenter(RegisterContract.View view) {
        super(view);
    }

    @Override
    public void register(String account, String password, String name) {
        // 调用启动方法 在start方法中默认启动loading
        start();

        RegisterContract.View view = getView();

        if(!checkMobile(account)){
            // 手机账号检查不过
            view.showError(R.string.data_account_register_invalid_parameter_mobile);
        }else if(password.length()<6){
            // 密码长度小于2位
            view.showError(R.string.data_account_register_invalid_parameter_password);
        }else if(name.length()<2){
            // 姓名长度小于两位
            view.showError(R.string.data_account_register_invalid_parameter_name);
        }else{
            //  进行网络请求
            // 构造model 请求调用
            RegisterModel model = new RegisterModel(account,password,name, Account.getPushId());
            AccountHelper.register(model,this);
        }

    }

    /**
     * 检查手机号是否合法
     * @param phone 电话号码
     */
    @Override
    public boolean checkMobile(String phone) {

        // 检查手机号不为空而且符合正则表达式
        return !TextUtils.isEmpty(phone)
                && Pattern.matches(Common.Constant.REGEX_MOBIE,phone);
    }


    @Override
    public void onDataLoaded(User user) {
        // 当网络请求好了 回送一个用户信息
        //  告知界面 注册成功
        final RegisterContract.View view = getView();
        if(view == null)
            return;

        //此时可能是从网络发送回来的信息 并不能保证在主线程里
        // 强制执行在主线程中
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.registerSuccess();
            }
        });

    }

    @Override
    public void onDataNotAvailable(@StringRes final int strRes) {
        // 网络请求告知注册失败
        final RegisterContract.View view = getView();
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
