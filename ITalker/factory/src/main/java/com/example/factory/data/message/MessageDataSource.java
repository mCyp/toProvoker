package com.example.factory.data.message;

import com.example.factory.data.DataSource;
import com.example.factory.data.DbDataSource;
import com.example.factory.model.db.Message;

/**
 * 消息的数据源的定义 他的实现是MessageRepository MessageGroupReository
 * 关注的对象是message这个表
 * Created by Administrator on 2017/9/1.
 */

public interface MessageDataSource extends DbDataSource<Message>{
}
