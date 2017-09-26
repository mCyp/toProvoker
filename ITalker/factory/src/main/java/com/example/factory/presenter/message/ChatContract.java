package com.example.factory.presenter.message;

import com.example.factory.model.db.Group;
import com.example.factory.model.db.Message;
import com.example.factory.model.db.User;
import com.example.factory.model.db.view.MemberUserModel;
import com.example.factory.presenter.BaseContract;

import java.util.List;

/**
 * 聊天的契约
 * Created by Administrator on 2017/9/1.
 */

public interface ChatContract {
    interface Presenter extends BaseContract.Presenter{
        // 发送文字消息
        void pushText(String content);
        // 发送语音
        void pushAudio(String path,long time);
        // 发送图片，可以支持批量图片的发送
        void pushImages(String[] paths);
        // 重新发送信息，有boolean确定值是否成功重新发送
        boolean rePush(Message message);
    }

    interface View<InitModel> extends BaseContract.RecyclerView<Presenter,Message>{
        // 初始化modek
        void onInit(InitModel model);
    }

    // 人聊天的界面
    interface UserView extends View<User>{

    }

    // 群聊天的界面
    interface GroupView extends View<Group>{
        // 显示是否是管理员
        void showAdminOption(boolean isAdmin);
        // 显示简单的成员信息
        void onInitGroupMembers(List<MemberUserModel> members,long moreCount);
    }
}
