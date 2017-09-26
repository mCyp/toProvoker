package com.example.factory.presenter.contact;

import com.example.factory.model.api.card.UserCard;
import com.example.factory.presenter.BaseContract;
import com.example.factory.presenter.account.LoginContract;

/**
 * 关注用户的逻辑实现
 * Created by Administrator on 2017/8/19.
 */

public interface FollowContract {
    interface Presenter extends BaseContract.Presenter{
        // 关注一个人
        void follow(String id);
    }

    interface View extends BaseContract.View<Presenter>{
        // 成功的情况下返回用户信息
        void onFollowSucceed(UserCard userCard);
    }
}
