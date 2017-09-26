package com.example.administrator.italker.push.frags.account;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.administrator.italker.push.R;
import com.example.administrator.italker.push.activities.MainActivity;
import com.example.administrator.italker.push.frags.assist.PermissionsFragment;
import com.example.common.app.Fragment;
import com.example.common.app.PresenterFragment;
import com.example.factory.presenter.account.LoginContract;
import com.example.factory.presenter.account.LoginPresenter;

import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.Loading;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 登陆的界面
 */
public class LoginFragment extends PresenterFragment<LoginContract.Presenter>
                                implements LoginContract.View{

    private AccountTrigger mAccountTrigger;

    @BindView(R.id.edit_phone)
    EditText mPhone;

    @BindView(R.id.edit_password)
    EditText mPassword;

    @BindView(R.id.loading)
    Loading mLoading;

    @BindView(R.id.btn_submit)
    Button mSubmit;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAccountTrigger = (AccountTrigger)context;
    }

    @Override
    protected LoginContract.Presenter initPresenter() {
        return new LoginPresenter(this);
    }

    @Override
    public void showError(@StringRes int str) {
        super.showError(str);

        // 载入框停止运行
        mLoading.stop();
        // 电话密码姓名输入框可以输入
        mPhone.setEnabled(true);
        mPassword.setEnabled(true);
        // 注册按钮可以提交
        mSubmit.setEnabled(true);
    }

    @Override
    public void showLoading() {
        super.showLoading();

        // 载入框开始运行
        mLoading.start();
        // 电话密码姓名输入框不可以输入
        mPhone.setEnabled(false);
        mPassword.setEnabled(false);
        // 注册按钮不可以提交
        mSubmit.setEnabled(false);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_login;
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick(){
        String phone = mPhone.getText().toString();
        String password = mPassword.getText().toString();
        mPresenter.login(phone,password);
    }

    @OnClick(R.id.txt_go_register)
    void onShowRegisterClick(){
        // 让AccountActivity进行界面切换
        mAccountTrigger.triggerView();
    }

    @Override
    public void loginSuccess() {
        // 注册成功 我们需要跳转到MainActivity里面 账户已经登录
        MainActivity.show(getContext());
        // 关闭当前界面
        getActivity().finish();
    }
}
