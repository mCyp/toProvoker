package com.example.factory.presenter.user;

import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.text.style.UpdateLayout;

import com.example.factory.Factory;
import com.example.factory.R;
import com.example.factory.data.DataSource;
import com.example.factory.data.helper.UserHelper;
import com.example.factory.model.api.card.UserCard;
import com.example.factory.model.api.user.UserUpdateModel;
import com.example.factory.model.db.User;
import com.example.factory.net.UploadHelper;
import com.example.factory.presenter.BasePresenter;
import com.example.factory.presenter.account.LoginContract;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * 更新逻辑的实现
 * Created by Administrator on 2017/8/18.
 */

public class UpdateInfoPresenter extends BasePresenter<UpdateInfoContract.View>
implements UpdateInfoContract.Presenter,DataSource.Callback<UserCard>{
    /**
     * 构造函数
     *
     * @param view
     */
    public UpdateInfoPresenter(UpdateInfoContract.View view) {
        super(view);
    }

    @Override
    public void update(final String photoFilePath, final String desc, final boolean isMan) {
        // 调用启动方法 默认启动loading
        start();

        final UpdateInfoContract.View view = getView();
        if(TextUtils.isEmpty(photoFilePath)||TextUtils.isEmpty(desc)){
            view.showError(R.string.data_account_update_invalid_parameter);
        }else{
            // 上传头像
            Factory.runOnAsync(new Runnable() {
                @Override
                public void run() {
                    String url = UploadHelper.uploadPortrait(photoFilePath);
                    if(TextUtils.isEmpty(url)){
                        view.showError(R.string.data_upload_error);
                    }else{
                        // 构建model
                        UserUpdateModel model = new UserUpdateModel("",url,desc
                                ,isMan? User.SEX_MAN:User.SEX_WOMAN);
                        // 进行网络请求的上传
                        UserHelper.update(model,UpdateInfoPresenter.this);
                    }
                }
            });

        }


    }

    @Override
    public void onDataLoaded(UserCard userCard) {
        // 当网络请求好了 回送一个用户信息
        //  告知界面 更新
        final UpdateInfoContract.View view = getView();
        if(view == null)
            return;

        //此时可能是从网络发送回来的信息 并不能保证在主线程里
        // 强制执行在主线程中
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.updateSucceed();
            }
        });
    }

    @Override
    public void onDataNotAvailable(@StringRes final int strRes) {
        // 当网络请求好了 回送一个用户信息
        //  告知界面 更新
        final UpdateInfoContract.View view = getView();
        if(view == null)
            return;

        //此时可能是从网络发送回来的信息 并不能保证在主线程里
        // 强制执行在主线程中
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.showError(strRes);
            }
        });
    }
}
