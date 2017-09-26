package com.example.factory.presenter.group;

import com.example.factory.model.Author;
import com.example.factory.presenter.BaseContract;

/**
 * 群组创建的契约
 * Created by Administrator on 2017/9/7.
 */

public interface GroupCreateContract {
    interface Presenter extends BaseContract.Presenter{
        // 创建的提交
        void create(String name,String desc,String picture);
        // 更改一个model选中的状态
        void changeSelect(ViewMode mode,boolean isSelected);
    }

    interface View extends BaseContract.RecyclerView<Presenter,ViewMode>{
        // 创建成功
        void onCreateSucceed();
    }

    class ViewMode{
        // 用户信息
        public Author author;
        // 是否被选中
        public boolean isSelected;
    }
}
