package com.example.factory.presenter.message;

import com.example.factory.model.db.Session;
import com.example.factory.model.db.User;
import com.example.factory.presenter.BaseContract;
import com.example.factory.presenter.contact.ContactContract;

/**
 * Created by Administrator on 2017/9/3.
 */

public interface SessionContract {
    // 什么都不需要做， 开始就调用start()方法就行
    interface Presenter extends BaseContract.Presenter{

    }

    // 都在基类中完成了
    interface View extends BaseContract.RecyclerView<Presenter,Session>{

    }
}
