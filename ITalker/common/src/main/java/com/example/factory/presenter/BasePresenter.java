package com.example.factory.presenter;

/**
 * Created by Administrator on 2017/8/13.
 */

public class BasePresenter<T extends BaseContract.View> implements BaseContract.Presenter{
    private T mView;

    /**
     * 构造函数
     * @param view
     */
    public BasePresenter(T view){
        setView(view);
    }

    /**
     * 设置一个View，子类可以复写完成
     * @param view
     */
    @SuppressWarnings("unchecked")
    protected void setView(T view){
        this.mView = view;
        this.mView.setPresenter(this);
    }

    /**
     * 给子类用的获取View的操作 不允许复写
     * @return
     */
    protected final T getView(){
        return mView;
    }

    @Override
    public void start() {
        // 开始的时候进行loading调用
        T view = mView;
        if(view != null){
            view.showLoading();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void destroy() {
        T view = mView;
        mView = null;
        if(view != null){
            // 把Presenter设置为NULL
            view.setPresenter(null);
        }
    }
}
