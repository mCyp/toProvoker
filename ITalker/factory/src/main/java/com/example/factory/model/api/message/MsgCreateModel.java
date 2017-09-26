package com.example.factory.model.api.message;

import com.example.factory.model.api.card.MessageCard;
import com.example.factory.model.db.Message;
import com.example.factory.persistance.Account;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Administrator on 2017/9/2.
 */

public class MsgCreateModel {
    private String id;
    private String content;
    private String attach;
    // 默认为常规的消息类型
    private int type = Message.TYPE_STR;
    private String receiveId;
    // 接受者类型 群、人
    // 默认为发送给人的消息类型
    private int receiveType = Message.RECEIVER_TYPE_NONE;

    private MsgCreateModel(){
        // 随机生成一个UUID
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }


    public String getAttach() {
        return attach;
    }


    public int getType() {
        return type;
    }

    public String getReceiveId() {
        return receiveId;
    }


    public int getReceiveType() {
        return receiveType;
    }




    // 当我们需要发送一个content的时候 刷新界面的问题

    private MessageCard card;
    // 返回一个文件
    public MessageCard buildCard(){
        if(card == null){
            MessageCard card = new MessageCard();
            card.setId(id);
            card.setContent(content);
            card.setAttach(attach);
            card.setType(type);
            card.setSenderId(Account.getUserId());

            if(receiveType == Message.RECEIVER_TYPE_GROUP){
                card.setGroupId(receiveId);
            }else {
                card.setReceiverId(receiveId);
            }

            // 通过当前状态建立的model就是一个状态为初步完成的model
            card.setStatus(Message.STATUS_CREATED);
            card.setCreateAt(new Date());
            this.card = card;
        }
        return this.card;
    }

    // 同步到卡片的最新状态
    public void refreshByCard() {
        if(card == null)
            return;
        // 刷新内容和附件信息
        this.content = card.getContent();
        this.attach = card.getAttach();
    }

    public static class Builder{
        private MsgCreateModel model;

        public Builder(){
            this.model = new MsgCreateModel();
        }

        // 获取接收者
        public Builder receiver(String receiverId,int receiverType){
            this.model.receiveId = receiverId;
            this.model.receiveType = receiverType;
            return this;
        }

        // 获取内容 并且获取文件传输的类型
        public Builder content(String content,int type){
            this.model.content = content;
            this.model.type = type;
            return this;
        }

        // 获取附件
        public Builder attach(String attach){
            this.model.attach = attach;
            return this;
        }

        public MsgCreateModel Build(){
            return this.model;
        }
    }

    /**
     * 把一个message消息 转化为一个创建状态的MsgCreateModel
     * @param message Message
     * @return MsgCreateModel
     */
    public static MsgCreateModel buildWithMessage(Message message){
        MsgCreateModel model = new MsgCreateModel();
        model.id = message.getId();
        model.content = message.getContent();
        model.type = message.getType();
        model.attach = message.getAttach();

        if(message.getReceiver() != null){
            // 如果接收者不为空
            model.receiveId = message.getReceiver().getId();
            model.receiveType = Message.RECEIVER_TYPE_NONE;
        }else {
            // 如果接收者为群
            model.receiveId = message.getGroup().getId();
            model.receiveType = Message.RECEIVER_TYPE_GROUP;
        }
        return model;
    }

}
