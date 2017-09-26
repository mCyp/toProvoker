package com.example.factory.data.message;

import android.support.annotation.NonNull;

import com.example.factory.data.BaseDbRepository;
import com.example.factory.data.DataSource;
import com.example.factory.model.db.Message;
import com.example.factory.model.db.Message_Table;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 跟某人的聊天列表
 * 关注的内容一定是他发送给我的，或者是我发送给这个人的
 * Created by Administrator on 2017/9/1.
 */

public class MessageRepository extends BaseDbRepository<Message>
        implements MessageDataSource {

    // 聊天对象的id
    private String receiverId;

    public MessageRepository(String receiverId){
        super();
        this.receiverId = receiverId;
    }

    @Override
    public void load(SucceedCallback<List<Message>> callback) {
        super.load(callback);

        SQLite.select()
                .from(Message.class)  // 从Message表里面查询
                .where(OperatorGroup.clause()
                        .and(Message_Table.sender_id.eq(receiverId))
                        .and(Message_Table.group_id.isNull()))
                .or(Message_Table.receiver_id.eq(receiverId))
                .orderBy(Message_Table.createAt,false) // 按时间倒序查询
                .limit(30) // 限制30条信息
                .async() // 异步操作
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Message message) {
        // receiverId如果是发送者 group为null的情况下一定是发送给我的信息
        // 我发出去的发送给某人的信息
        return (receiverId.equalsIgnoreCase(message.getSender().getId())
                &&message.getGroup() == null)
                ||(message.getReceiver()!=null
                && receiverId.equalsIgnoreCase(message.getReceiver().getId()) );
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {
        // 反转返回的集合
        Collections.reverse(tResult);
        super.onListQueryResult(transaction, tResult);
    }
}
