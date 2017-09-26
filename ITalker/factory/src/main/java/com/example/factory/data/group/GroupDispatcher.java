package com.example.factory.data.group;

import com.example.factory.data.helper.DbHelper;
import com.example.factory.data.helper.GroupHelper;
import com.example.factory.data.helper.UserHelper;
import com.example.factory.data.user.UserDispatcher;
import com.example.factory.model.api.card.GroupCard;
import com.example.factory.model.api.card.GroupMemberCard;
import com.example.factory.model.db.Group;
import com.example.factory.model.db.GroupMember;
import com.example.factory.model.db.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/8/23.
 */

public class GroupDispatcher implements GroupCenter {
    private static GroupCenter instance;
    // 单线程池 处理卡片的一个个消息
    private final Executor executor = Executors.newSingleThreadExecutor();

    public static GroupCenter instance(){
        if(instance == null){
            synchronized (GroupDispatcher.class){
                if(instance == null)
                    instance = new GroupDispatcher();
            }
        }
        return  instance;
    }

    @Override
    public void dispatch(GroupCard... cards) {
        if(cards==null || cards.length == 0)
            return;
        executor.execute(new GroupDispatcher.GroupHandler(cards));
    }

    @Override
    public void dispatch(GroupMemberCard... cards) {
        if(cards==null || cards.length == 0)
            return;
        executor.execute(new GroupDispatcher.GroupMemberRspHander(cards));
    }

    private class GroupMemberRspHander implements Runnable{

        private final GroupMemberCard[] cards;

        public GroupMemberRspHander(GroupMemberCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<GroupMember> members = new ArrayList<>();
            for (GroupMemberCard card : cards) {
                // 成员对应的信息
                User user = UserHelper.search(card.getUserId());
                // 搜索群的id
                Group group = GroupHelper.find(card.getGroupId());

                if(user != null && group != null){
                    GroupMember groupMember = card.build(group,user);
                    members.add(groupMember);
                }
            }
            if(members.size() > 0){
                DbHelper.save(GroupMember.class,members.toArray(new GroupMember[0]));
            }
        }
    }

    /**
     * 把群card类处理为群db类
     */
    private class GroupHandler implements Runnable{

        private final GroupCard[] cards;

        public GroupHandler(GroupCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<Group> groups = new ArrayList<>();
            for (GroupCard card : cards) {
                // 搜索管理员
                User owner = UserHelper.search(card.getOwnerId());
                if(owner != null){
                    Group group = card.build(owner);
                    groups.add(group);
                }
            }

            if(groups.size() > 0){
                DbHelper.save(Group.class, groups.toArray(new Group[0]));
            }
        }
    }
}
