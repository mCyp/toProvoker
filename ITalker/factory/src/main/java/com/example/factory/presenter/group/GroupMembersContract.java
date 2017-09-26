package com.example.factory.presenter.group;

import com.example.factory.model.db.view.MemberUserModel;
import com.example.factory.presenter.BaseContract;

/**
 * 查询群成员的契约
 * Created by Administrator on 2017/9/14.
 */

public interface GroupMembersContract {
    interface Presenter extends BaseContract.Presenter{
        // 具有一个刷新的方法
        void refresh();
    }
    // 界面
    interface View extends BaseContract.RecyclerView<Presenter,MemberUserModel>{
        // 返回群id
        String getmGroupId();
    }
}
