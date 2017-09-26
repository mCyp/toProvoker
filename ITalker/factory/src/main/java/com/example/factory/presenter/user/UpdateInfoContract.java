package com.example.factory.presenter.user;

import com.example.factory.presenter.BaseContract;

/**
 * Created by Administrator on 2017/8/17.
 */

public interface UpdateInfoContract {
    interface Presenter extends BaseContract.Presenter{
        // 更新
        void update(String photoFilePath,String desc,boolean isMan);
    }

    interface View extends BaseContract.View<Presenter>{
        // 更新成功的回调接口
        void updateSucceed();
    }
}
