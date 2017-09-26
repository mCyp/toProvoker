package com.example.factory.data.message;

import com.example.factory.model.api.card.MessageCard;

/**
 * 消息中心 进行消息卡片的消费
 * Created by Administrator on 2017/8/23.
 */

public interface MessageCenter {
    // 消息中心 进行消息卡片的消费
    void dispatch(MessageCard... cards);
}
