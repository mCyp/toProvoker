package com.example.factory.presenter;

import android.support.annotation.StringRes;

import com.example.common.widget.recycler.RecyclerAdapter;

/**
 * Mvp模式中公共的基本的契约
 * Created by Administrator on 2017/8/13.
 */

public interface BaseContract {
    // 基本的界面的职责
    interface View<T extends Presenter>{
        //公共的：登陆出错提示
        void showError(@StringRes int str);
        //  公共的：进度条提示
        void showLoading();
        // 支持设置一个Presenter
        void setPresenter(T presenter);
    }

    // 基本的Presenter职责
    interface Presenter{
        // 公用的开始方法
        void start();
        // 公用的销毁触发
        void destroy();
    }

    // 基本的一个列表View 的职责
    interface RecyclerView<T extends Presenter,ViewMode> extends View<T>{
        // 界面端只能刷新数据集合，不能精确到每一条数据
        //void onDone(List<User> users);

        // RecyclerAdapter既可以实现局部刷新 拿到一个适配器 然后自主的进行刷新
        RecyclerAdapter<ViewMode> getRecyclerAdapter();

        // 当适配器数据更改的时候触发
        void onAdapterDataChanged();
    }

}
