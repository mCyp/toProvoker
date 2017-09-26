package com.example.factory.presenter.contact;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.util.DiffUtil;

import com.example.common.widget.recycler.RecyclerAdapter;
import com.example.factory.data.DataSource;
import com.example.factory.data.helper.UserHelper;
import com.example.factory.data.user.ContactDataSource;
import com.example.factory.data.user.ContactRepository;
import com.example.factory.model.api.card.UserCard;
import com.example.factory.model.db.AppDatabase;
import com.example.factory.model.db.User;
import com.example.factory.model.db.User_Table;
import com.example.factory.persistance.Account;
import com.example.factory.presenter.BasePresenter;
import com.example.factory.presenter.BaseRecyclerPresenter;
import com.example.factory.presenter.BaseSourcePresenter;
import com.example.factory.utils.DiffUiDataCallback;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * 联系人的Presenter的实现
 * Created by Administrator on 2017/8/20.
 */

public class ContactPresenter extends BaseSourcePresenter<User,User,ContactDataSource,ContactContract.View>
        implements ContactContract.Presenter ,DataSource.SucceedCallback<List<User>>{
    /**
     * 构造函数
     *
     * @param view
     */
    public ContactPresenter(ContactContract.View view) {
        // 初始化数据仓库
        super(view,new ContactRepository());
    }

    @Override
    public void start() {
        super.start();

        // 加载网络数据库数据
        UserHelper.refreshContacts();
    }



    @Override
    public void onDataLoaded(List<User> users) {
        // 数据变更 最终会通知到这里来
        final ContactContract.View view = getView();
        if(view == null)
            return;

        RecyclerAdapter adapter = view.getRecyclerAdapter();
        List<User> old =adapter.getItems();


        // 进行数据对比
        DiffUtil.Callback callback = new DiffUiDataCallback<>(old,users);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        refreshData(result,users);

    }


}
