package com.example.factory.presenter.message;

import android.support.v7.util.DiffUtil;
import android.text.TextUtils;

import com.example.factory.data.helper.MessageHelper;
import com.example.factory.data.message.MessageDataSource;
import com.example.factory.model.api.message.MsgCreateModel;
import com.example.factory.model.db.Message;
import com.example.factory.persistance.Account;
import com.example.factory.presenter.BaseSourcePresenter;
import com.example.factory.utils.DiffUiDataCallback;

import java.util.List;

/**
 * 基础的发信息的逻辑的封装
 * Created by Administrator on 2017/9/1.
 */

@SuppressWarnings("WeakerAccess")
public class ChatPresenter<View extends ChatContract.View> extends BaseSourcePresenter<Message,Message,MessageDataSource,View>
implements ChatContract.Presenter{

    // 如果是发送给人那么要获取接受者的id
    protected String mReceiverId;
    // 接受的类型 如果是群组消息的话
    protected int mReceiverType;

    /**
     * 构造函数
     *
     * @param view
     * @param source
     */
    public ChatPresenter(View view, MessageDataSource source,String receiverId,int receiverType) {
        super(view, source);
        this.mReceiverId = receiverId;
        this.mReceiverType = receiverType;
    }

    @Override
    public void pushText(String content) {
        // 构建一个发送消息
        MsgCreateModel model = new MsgCreateModel.Builder()
                .receiver(mReceiverId,mReceiverType)
                .content(content,Message.TYPE_STR)
                .Build();

        // 进行网络发送
        MessageHelper.push(model);
    }

    @Override
    public void pushAudio(String path,long time) {
       if(TextUtils.isEmpty(path)){
            return;
        }

        // 构建一个发送消息
        MsgCreateModel model = new MsgCreateModel.Builder()
                .receiver(mReceiverId,mReceiverType)
                .content(path,Message.TYPE_AUDIO)
                .attach(String.valueOf(time))
                .Build();

        // 进行网络发送
        MessageHelper.push(model);
    }

    @Override
    public void pushImages(String[] paths) {
        if(paths == null||paths.length == 0)
            return;
        for (String path : paths) {
            // 构建一个发送消息
            MsgCreateModel model = new MsgCreateModel.Builder()
                    .receiver(mReceiverId,mReceiverType)
                    .content(path,Message.TYPE_PIC)
                    .Build();

            // 进行网络发送
            MessageHelper.push(model);
        }

    }

    @Override
    public boolean rePush(Message message) {
        if(Account.getUserId().equalsIgnoreCase(message.getSender().getId())
                && message.getStatus() == Message.STATUS_FAILED){
            // 重新设置状态
            message.setStatus(Message.STATUS_CREATED);
            // 构建model
            MsgCreateModel model = MsgCreateModel.buildWithMessage(message);
            MessageHelper.push(model);
            return true;
        }
        return false;
    }


    @SuppressWarnings("unchecked")
    @Override
    public void onDataLoaded(List<Message> messages) {
        ChatContract.View view = getView();
        if(view == null)
            return;

        // 获取老数据
        List<Message> old = view.getRecyclerAdapter().getItems();

        // 差异计算
        DiffUiDataCallback<Message> callback = new DiffUiDataCallback<>(old,messages);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        refreshData(result,messages);
    }
}
