package com.example.factory.data.message;

import android.support.annotation.NonNull;

import com.example.factory.data.BaseDbRepository;
import com.example.factory.model.db.Message;
import com.example.factory.model.db.Message_Table;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.Collections;
import java.util.List;

/**
 * 群的聊天列表
 * 关注的内容一定是我发送给这个群的 或者群里的其他人发送的信息
 * Created by Administrator on 2017/9/1.
 */

public class MessageGroupRepository extends BaseDbRepository<Message>
        implements MessageDataSource {

    // 聊天群的id
    private String receiverId;

    public MessageGroupRepository(String receiverId){
        super();
        this.receiverId = receiverId;
    }

    @Override
    public void load(SucceedCallback<List<Message>> callback) {
        super.load(callback);

        // 查询发送到群的信息
        SQLite.select()
                .from(Message.class)  // 从Message表里面查询
                .where(Message_Table.group_id.eq(receiverId))
                .orderBy(Message_Table.createAt,false) // 按时间倒序查询
                .limit(30) // 限制30条信息
                .async() // 异步操作
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Message message) {
        // 什么都不显示
       return message.getGroup() != null &&
               receiverId.equalsIgnoreCase(message.getGroup().getId());
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {
        // 反转返回的集合
        Collections.reverse(tResult);
        super.onListQueryResult(transaction, tResult);
    }
}
