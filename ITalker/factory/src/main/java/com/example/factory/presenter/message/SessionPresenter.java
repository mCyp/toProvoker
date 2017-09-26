package com.example.factory.presenter.message;


import android.support.v7.util.DiffUtil;

import com.example.factory.data.message.SessionDataSource;
import com.example.factory.data.message.SessionRepository;
import com.example.factory.model.db.Session;
import com.example.factory.presenter.BaseSourcePresenter;
import com.example.factory.utils.DiffUiDataCallback;

import java.util.List;

/**
 * 拉取最近聊天记录的Presenter
 * Created by Administrator on 2017/9/3.
 */

public class SessionPresenter extends BaseSourcePresenter<Session,Session,SessionDataSource,SessionContract.View>
implements SessionContract.Presenter{


    /**
     * 构造函数
     *
     * @param view
     */
    public SessionPresenter(SessionContract.View view) {
        super(view, new SessionRepository());
    }

    @Override
    public void onDataLoaded(List<Session> sessions) {
        // 获取界面
        SessionContract.View view = getView();
        if(view == null)
            return;

        // 获取旧的数据
        List<Session> old = getView().getRecyclerAdapter().getItems();
        DiffUiDataCallback<Session> callback = new DiffUiDataCallback<>(old,sessions);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        // 刷新数据
        refreshData(result,sessions);
    }
}