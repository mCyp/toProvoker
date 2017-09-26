package com.example.factory.data.message;

import android.text.TextUtils;

import com.example.factory.data.helper.DbHelper;
import com.example.factory.data.helper.GroupHelper;
import com.example.factory.data.helper.MessageHelper;
import com.example.factory.data.helper.UserHelper;
import com.example.factory.model.api.card.MessageCard;
import com.example.factory.model.db.Group;
import com.example.factory.model.db.Message;
import com.example.factory.model.db.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 消息中心的实现
 * Created by Administrator on 2017/8/23.
 */

public class MessageDispatcher implements MessageCenter{

    private static MessageCenter instance;
    // 单线程池 处理卡片的一个个消息
    private final Executor executor = Executors.newSingleThreadExecutor();

    public static MessageCenter instance(){
        if(instance == null){
            synchronized (MessageDispatcher.class){
                if(instance == null)
                    instance = new MessageDispatcher();
            }
        }
        return  instance;
    }

    @Override
    public void dispatch(MessageCard... cards) {
        if(cards==null || cards.length == 0)
            return;
        executor.execute(new MessageCardHandler(cards));
    }

    private class MessageCardHandler implements Runnable{

        private MessageCard[] cards;

        public MessageCardHandler(MessageCard[] cards) {
            this.cards = cards;
        }

        // 消息卡片的线程调度会处理run方法
        @Override
        public void run() {
            List<Message> messages = new ArrayList<>();
            // 遍历cards
            for (MessageCard card : cards) {
                // 卡片基础信息过滤 错误卡片直接过滤
                if(card == null || TextUtils.isEmpty(card.getSenderId())
                        || TextUtils.isEmpty(card.getId())
                        || (TextUtils.isEmpty(card.getReceiverId())
                        && TextUtils.isEmpty(card.getGroupId())
                        ))
                    continue;

                // 消息卡片有可能是推送过来的 也有可能是本地造的
                // 推送过来的服务器一定有 我们可以查询到 本地有可能有 也有可能没有
                // 如果是造的 先存储本地 后发送服务器
                // 发送消息流程： 写消息->存储本地->发送网络->网络返回->刷新本地状态
                Message message = MessageHelper.findFromLocal(card.getId());
                if(message != null){
                    // 消息本身字段从发送后就不再变化了
                    // 本地有， 同事显示消息的状态为完成状态， 则不必处理
                    // 因为此时回来的消息 一定和本地一样

                    // 如果本地消息已经完成则不做处理
                    if(message.getStatus() == Message.STATUS_DONE)
                        continue;

                    // 新状态为完成才更新服务器 不然不做更新
                    if(card.getStatus() == Message.STATUS_DONE){
                        // 代表为网络发送成功 此时需要修改时间为服务器时间
                        message.setCreateAt(card.getCreateAt());
                    }

                    // 更新一下变化的内容

                    message.setContent(card.getContent());
                    message.setStatus(card.getStatus());
                    message.setAttach(card.getAttach());
                }else{
                    // 没找到本地消息 初次在数据库中存储
                    User sender = UserHelper.search(card.getSenderId());
                    User receiver = null;
                    Group group = null;
                    if(!TextUtils.isEmpty(card.getReceiverId())){
                        receiver = UserHelper.search(card.getReceiverId());
                    }else if(!TextUtils.isEmpty(card.getGroupId())){
                        group = GroupHelper.findFromLocal(card.getGroupId());
                    }
                    if(receiver == null && group == null&& sender != null)
                        continue;
                    message = card.build(sender,receiver,group);
                }

                messages.add(message);

            }
            if(messages.size()>0)
                DbHelper.save(Message.class,messages.toArray(new Message[0]));
        }
    }
}
