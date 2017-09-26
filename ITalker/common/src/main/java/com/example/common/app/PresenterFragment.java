package com.example.common.app;

import android.content.Context;
import android.support.annotation.StringRes;

import com.example.factory.presenter.BaseContract;

/**
 * Created by Administrator on 2017/8/13.
 */

public abstract class PresenterFragment<Presenter extends BaseContract.Presenter> extends Fragment implements BaseContract.View<Presenter> {
    public Presenter mPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // 在界面onAttach之后就初始化Presenter
        initPresenter();
    }

    /**
     * 初始化一个Presenter
     * @return
     */
    protected abstract Presenter initPresenter();

    @Override
    public void showError(@StringRes int str) {
        // 显示错误,优先使用占位布局显示错误
        if(mPlaceHolderView != null){
            mPlaceHolderView.triggerError(str);
        }else {
            Application.showToast(str);
        }
    }

    @Override
    public void showLoading() {
        if(mPlaceHolderView != null){
            mPlaceHolderView.triggerLoading();
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mPresenter != null){
            mPresenter.destroy();
        }
    }
}
