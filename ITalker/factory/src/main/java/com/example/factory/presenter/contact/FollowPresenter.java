package com.example.factory.presenter.contact;

import android.support.annotation.StringRes;

import com.example.factory.data.DataSource;
import com.example.factory.data.helper.UserHelper;
import com.example.factory.model.api.card.UserCard;
import com.example.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * 关注的逻辑实现
 * Created by Administrator on 2017/8/19.
 */

public class FollowPresenter extends BasePresenter<FollowContract.View>
implements FollowContract.Presenter,DataSource.Callback<UserCard>{
    /**
     * 构造函数
     *
     * @param view
     */
    public FollowPresenter(FollowContract.View view) {
        super(view);
    }

    @Override
    public void follow(String id) {
        start();

        UserHelper.follow(id,this);
    }

    @Override
    public void onDataLoaded(final UserCard userCard) {
        final FollowContract.View view = getView();
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.onFollowSucceed(userCard);
            }
        });
    }

    @Override
    public void onDataNotAvailable(@StringRes final int strRes) {
        final FollowContract.View view = getView();
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.showError(strRes);
            }
        });
    }
}
