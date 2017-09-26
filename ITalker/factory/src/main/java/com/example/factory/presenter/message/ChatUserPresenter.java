package com.example.factory.presenter.message;

import com.example.factory.data.helper.UserHelper;
import com.example.factory.data.message.MessageDataSource;
import com.example.factory.data.message.MessageRepository;
import com.example.factory.model.db.Message;
import com.example.factory.model.db.User;

/**
 * Created by Administrator on 2017/9/1.
 */

public class ChatUserPresenter extends ChatPresenter<ChatContract.UserView>
        implements ChatContract.Presenter{


    /**
     * 构造函数
     *
     * @param view
     * @param receiverId
     */
    public ChatUserPresenter(ChatContract.UserView view,String receiverId) {
        // View 数据源 接收者Id 接收者的类型
        super(view, new MessageRepository(receiverId), receiverId, Message.RECEIVER_TYPE_NONE);
    }

    @Override
    public void start() {
        super.start();

        // 从本地拿个人的信息
        User receiver = UserHelper.findFromLocal(mReceiverId);
        getView().onInit(receiver);
    }
}
