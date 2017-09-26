package com.example.factory.presenter.group;

import com.example.factory.model.db.Group;
import com.example.factory.model.db.User;
import com.example.factory.presenter.BaseContract;

/**
 * 我的群列表的契约
 * Created by Administrator on 2017/8/20.
 */

public interface GroupsContract {
    // 什么都不需要做， 开始就调用start()方法就行
    interface Presenter extends BaseContract.Presenter{

    }

    // 都在基类中完成了
    interface View extends BaseContract.RecyclerView<Presenter,Group>{

    }
}
