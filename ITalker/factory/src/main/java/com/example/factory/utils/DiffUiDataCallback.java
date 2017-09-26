package com.example.factory.utils;

import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/8/21.
 */

public class DiffUiDataCallback<T extends DiffUiDataCallback.UiDataDiffer<T>> extends DiffUtil.Callback {
    private List<T> mOldList,mNewList;

    public DiffUiDataCallback(List<T> mOldList, List<T> mNewList) {
        this.mOldList = mOldList;
        this.mNewList = mNewList;
    }

    @Override
    public int getOldListSize() {
        // 旧的数据大小
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        // 新的数据大小
        return mNewList.size();
    }

    // 表明两个雷是否就是同一个东一
    @Override
    public boolean areItemsTheSame(int oldItemPositon, int newItemPositon) {
        T oldBean = mOldList.get(oldItemPositon);
        T newBean = mNewList.get(newItemPositon);
        return newBean.isSame(oldBean);
    }

    // 经过相等判断之后，进一步判断是否有数据更改
    // 比如，同一个用户后的两个不同实例,其中的name字段不同
    @Override
    public boolean areContentsTheSame(int oldItemPositon, int newItemPositon) {
        T oldBean = mOldList.get(oldItemPositon);
        T newBean = mNewList.get(newItemPositon);
        return newBean.isUiContentSame(oldBean);
    }

    // 进行数据比较的类型
    // 泛型的目的 是和你自己一样的数据类型进行比较
    public interface UiDataDiffer<T>{
        // 传递一个旧的数据给你，问你是否和你表示的是同一个数据
        boolean isSame(T old);

        // 你和旧的数据对比，内容是否相同
        boolean isUiContentSame(T old);
    }


}
