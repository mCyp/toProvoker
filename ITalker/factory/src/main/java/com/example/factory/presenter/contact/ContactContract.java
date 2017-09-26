package com.example.factory.presenter.contact;

import com.example.common.widget.recycler.RecyclerAdapter;
import com.example.factory.model.db.User;
import com.example.factory.presenter.BaseContract;

import java.util.List;

/**
 * Created by Administrator on 2017/8/20.
 */

public interface ContactContract {
    // 什么都不需要做， 开始就调用start()方法就行
    interface Presenter extends BaseContract.Presenter{

    }

    // 都在基类中完成了
    interface View extends BaseContract.RecyclerView<Presenter,User>{

    }
}
