package com.example.factory.data.message;

import android.support.annotation.NonNull;

import com.example.factory.data.BaseDbRepository;
import com.example.factory.model.db.Session;
import com.example.factory.model.db.Session_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.Collections;
import java.util.List;

/**
 * 最近聊天的仓库，是对
 * 是对SessionDataSource的实现
 * Created by Administrator on 2017/9/3.
 */

public class SessionRepository  extends BaseDbRepository<Session> implements SessionDataSource{

    @Override
    public void load(SucceedCallback<List<Session>> callback) {
        super.load(callback);
        // 数据库查询
        SQLite.select()
                .from(Session.class)
                .orderBy(Session_Table.modifyAt,false)
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Session session) {
        // 所有的会话我都需要 不需要过滤
        return true;
    }

    @Override
    protected void insert(Session session) {
        dataList.addFirst(session);
    }

    @Override
    protected void replace(int index, Session session) {
        dataList.remove(index);
        dataList.addFirst(session);
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Session> tResult) {

        // 进行一次反转
        Collections.reverse(tResult);

        super.onListQueryResult(transaction, tResult);


    }
}

