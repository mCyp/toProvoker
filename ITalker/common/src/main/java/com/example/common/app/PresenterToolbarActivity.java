package com.example.common.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;

import com.example.common.R;
import com.example.factory.presenter.BaseContract;

/**
 * Created by Administrator on 2017/8/21.
 */

public abstract class PresenterToolbarActivity<Presenter extends BaseContract.Presenter>
        extends ToolbarActivity  implements BaseContract.View<Presenter>{
    protected Presenter mPresenter;
    protected ProgressDialog mLoadingDialog;

    @Override
    protected void initBefore() {
        super.initBefore();
        // 初始化Presenter
        initPresenter();
    }

    /**
     * 初始化一个Presenter
     * @return
     */
    protected abstract Presenter initPresenter();

    @Override
    public void showError(@StringRes int str) {
        // 不管我怎么样 先隐藏我自己
        hideDialogLoading();

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
        }else {
            ProgressDialog dialog = mLoadingDialog;
            if(dialog == null){
                dialog = new ProgressDialog(this,R.style.AppTheme_Dialog_Alert_Light);
                // 不可触摸取消
                dialog.setCanceledOnTouchOutside(false);
                // 可强制取消
                dialog.setCancelable(true);
                // 设置监听事件
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // 结束当前界面
                        finish();
                    }
                });
                mLoadingDialog = dialog;
            }
            dialog.setMessage(getText(R.string.prompt_loading));
            dialog.show();
        }
    }

    protected void hideDialogLoading(){
        ProgressDialog dialog = mLoadingDialog;
        if(dialog != null) {
            mLoadingDialog = null;
            dialog.dismiss();
        }
    }

    public void hideLoading(){
        // 不管我怎么样 先隐藏我自己
        hideDialogLoading();

        if(mPlaceHolderView != null){
            mPlaceHolderView.triggerOk();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPresenter != null){
            mPresenter.destroy();
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        mPresenter = presenter;
    }

}
