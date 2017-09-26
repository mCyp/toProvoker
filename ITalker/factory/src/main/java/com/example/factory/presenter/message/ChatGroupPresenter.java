package com.example.factory.presenter.message;

import com.example.factory.data.helper.GroupHelper;
import com.example.factory.data.helper.UserHelper;
import com.example.factory.data.message.MessageGroupRepository;
import com.example.factory.data.message.MessageRepository;
import com.example.factory.model.db.Group;
import com.example.factory.model.db.GroupMember;
import com.example.factory.model.db.Message;
import com.example.factory.model.db.User;
import com.example.factory.model.db.view.MemberUserModel;
import com.example.factory.persistance.Account;

import java.util.List;

/**
 * Created by Administrator on 2017/9/1.
 */

public class ChatGroupPresenter extends ChatPresenter<ChatContract.GroupView>
        implements ChatContract.Presenter{


    /**
     * 构造函数
     *
     * @param view
     * @param receiverId
     */
    public ChatGroupPresenter(ChatContract.GroupView view,String receiverId) {
        // View 数据源 接收者Id 接收者的类型
        super(view, new MessageGroupRepository(receiverId), receiverId, Message.RECEIVER_TYPE_GROUP);
    }

    @Override
    public void start() {
        super.start();

        // 从本地拿个人的信息
        Group group = GroupHelper.findFromLocal(mReceiverId);
        if(group != null){
            // 初始化信息
            ChatContract.GroupView view = getView();

            // 判断是否是创建者
            // TODO 这样判断是管理员是错误的 这只是创建者 管理员包括创建者和管理员
            boolean isAdmin = Account.getUserId().equalsIgnoreCase(group.getOwner().getId());
            // 在界面上显示是否是管理员
            view.showAdminOption(isAdmin);
            // 基础信息初始化
            view.onInit(group);

            // 显示成员和数量
            List<MemberUserModel> models = group.getLatelyGroupMembers();
            // 成员数量的初始化
            final long memberCount = group.getGroupMemberCount();
            long moreCount = memberCount - models.size();
            view.onInitGroupMembers(models,moreCount);
        }
    }
}
