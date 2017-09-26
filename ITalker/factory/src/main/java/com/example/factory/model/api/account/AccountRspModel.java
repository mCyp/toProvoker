package com.example.factory.model.api.account;

import com.example.factory.model.db.User;

/**
 * Created by Administrator on 2017/8/14.
 */

public class AccountRspModel {
    // 用户的卡片信息
    private User user;
    // 用户的账户信息
    private String account;
    // 登录状态信息
    // 可以根据Token获取用户所有的信息
    private String token;
    // 判断是否绑定
    private boolean isBind;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isBind() {
        return isBind;
    }

    public void setBind(boolean bind) {
        isBind = bind;
    }

    @Override
    public String toString() {
        return "AccountRspModel{" +
                "user=" + user +
                ", account='" + account + '\'' +
                ", token='" + token + '\'' +
                ", isBind=" + isBind +
                '}';
    }
}
