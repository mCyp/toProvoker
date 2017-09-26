package com.example.factory.data;

import android.support.annotation.StringRes;

import java.util.List;

/**
 * 数据源接口
 * Created by Administrator on 2017/8/13.
 */

public interface DataSource {

    /**
     * 返回任意结果的回调接口
     * @param <T> 任意参数
     */
    interface Callback<T> extends SucceedCallback<T>,FailedCallback {

    }


    /**
     * 只关注成功的接口
     * @param <T> 任意参数
     */
    interface SucceedCallback<T>{
        // 数据加载成功，网络请求成功
        void onDataLoaded(T t);
    }

    /**
     * 只关注失败的接口
     */
    interface FailedCallback{
        // 数据加载失败，网络请求失败
        void onDataNotAvailable(@StringRes int strRes);
    }

    /**
     * 销毁操作
     */
    void dispose();

}
