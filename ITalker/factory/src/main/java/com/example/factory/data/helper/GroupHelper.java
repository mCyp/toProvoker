package com.example.factory.data.helper;

import com.example.factory.Factory;
import com.example.factory.R;
import com.example.factory.data.DataSource;
import com.example.factory.model.api.RspModel;
import com.example.factory.model.api.card.GroupCard;
import com.example.factory.model.api.card.GroupMemberCard;
import com.example.factory.model.api.card.UserCard;
import com.example.factory.model.api.group.GroupCreateModel;
import com.example.factory.model.db.Group;
import com.example.factory.model.db.GroupMember;
import com.example.factory.model.db.GroupMember_Table;
import com.example.factory.model.db.Group_Table;
import com.example.factory.model.db.User;
import com.example.factory.model.db.User_Table;
import com.example.factory.model.db.view.MemberUserModel;
import com.example.factory.net.Network;
import com.example.factory.net.RemoteService;
import com.example.factory.persistance.Account;
import com.example.utils.CollectionUtil;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 对群的一个简单的辅助工具类
 * Created by Administrator on 2017/8/23.
 */

public class GroupHelper {
    public static Group find(String groupId) {
        Group group = findFromLocal(groupId);
        if(group == null)
            group = findFromNet(groupId);
        return group;
    }

    // 从本地找group
    public static Group findFromLocal(String groupId) {
        // 查询群的信息 从本地获取
        return SQLite.select()
                .from(Group.class)
                .where(Group_Table.id.eq(groupId))
                .querySingle();
    }

    // 从网络找group
    public static Group findFromNet(String groupId) {
        // 查询群的信息 从本地获取
        RemoteService remoteService = Network.remote();
        try {
            Response<RspModel<GroupCard>> response = remoteService.groupFind(groupId).execute();
            GroupCard groupCard = response.body().getResult();
            if(groupCard != null){
                // 唤起进行保存的操作
                Factory.getGroupCenter().dispatch(groupCard);

                User user = UserHelper.search(groupCard.getOwnerId());
                if(user != null) {
                    Group group = groupCard.build(user);
                    return group;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 群的创建
    public static void createGroup(GroupCreateModel model, final DataSource.Callback<GroupCard> callback) {
        RemoteService service = Network.remote();
        service.groupCreate(model)
                .enqueue(new Callback<RspModel<GroupCard>>() {
                    @Override
                    public void onResponse(Call<RspModel<GroupCard>> call, Response<RspModel<GroupCard>> response) {
                        // 请求返回 得到我们的全局model
                        // 内部使用的是Gson 解析
                        RspModel<GroupCard> rspModel = response.body();
                        if(rspModel.success()){
                            GroupCard groupCard = rspModel.getResult();

                            // 唤起进行保存的操作
                            Factory.getGroupCenter().dispatch(groupCard);
                            callback.onDataLoaded(groupCard);
                        }else {
                            Factory.decodeRspCode(rspModel,callback);
                        }
                    }

                    @Override
                    public void onFailure(Call<RspModel<GroupCard>> call, Throwable t) {
                        // 网络请求失败
                        callback.onDataNotAvailable(R.string.data_network_error);
                    }
                });
    }

    // 群的搜索
    public static Call search(String  name, final DataSource.Callback<List<GroupCard>> callback) {
        RemoteService service = Network.remote();
        Call<RspModel<List<GroupCard>>> call = service.groupSearch(name);

        call.enqueue(new Callback<RspModel<List<GroupCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupCard>>> call, Response<RspModel<List<GroupCard>>> response) {
                // 请求返回 得到我们的全局model
                // 内部使用的是Gson 解析
                RspModel<List<GroupCard>> rspModel = response.body();
                if (rspModel.success()) {
                    callback.onDataLoaded(rspModel.getResult());
                } else {
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<GroupCard>>> call, Throwable t) {
                // 网络请求失败
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });

        // 返回当前的call 防止用户多次点击 前面的数据还没有回来
        return call;
    }

    // 刷新群组的操作，不需要callback 直接存储到数据库
    // 并通过数据库观察者进行更新
    // 界面更新的时候然后对比，进行差异更新。
    public static void refreshGroups(){
        RemoteService service = Network.remote();
        service.groups("").enqueue(new Callback<RspModel<List<GroupCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupCard>>> call, Response<RspModel<List<GroupCard>>> response) {
                // 请求返回 得到我们的全局model
                // 内部使用的是Gson 解析
                RspModel<List<GroupCard>> rspModel = response.body();
                if (rspModel.success()) {
                    List<GroupCard> cards = rspModel.getResult();
                    if(cards != null && cards.size() > 0){
                        // 进行调度显示
                        Factory.getGroupCenter().dispatch(cards.toArray(new GroupCard[0]));
                    }
                } else {
                    Factory.decodeRspCode(rspModel, null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<GroupCard>>> call, Throwable t) {
                // 不做任何事情
            }
        });
    }


    // 获取一个群的成员数量
    public static long getMemberCount(String id) {
        return SQLite.selectCountOf()
                .from(GroupMember.class)
                .where(GroupMember_Table.group_id.eq(id))
                .count();
    }

    // 从网络刷新一个群的成员信息
    public static void refreshGroupMembers(Group group) {
        RemoteService service = Network.remote();
        service.groupMembers(group.getId()).enqueue(new Callback<RspModel<List<GroupMemberCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupMemberCard>>> call, Response<RspModel<List<GroupMemberCard>>> response) {
                // 请求返回 得到我们的全局model
                // 内部使用的是Gson 解析
                RspModel<List<GroupMemberCard>> rspModel = response.body();
                if (rspModel.success()) {
                    List<GroupMemberCard> memberCards = rspModel.getResult();
                    if(memberCards != null && memberCards.size() > 0){
                        // 进行调度显示
                        Factory.getGroupCenter().dispatch(memberCards.toArray(new GroupMemberCard[0]));
                    }
                } else {
                    Factory.decodeRspCode(rspModel, null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<GroupMemberCard>>> call, Throwable t) {
                // 不做任何事情
            }
        });
    }

    // 关联一个用户和群成员的表，返回一个MemberUserModel临时的查询表
    public static List<MemberUserModel> getMemberUsers(String groupId, int size) {
        return SQLite.select(GroupMember_Table.alias.withTable().as("alias"),
                User_Table.id.withTable().as("id"),
                User_Table.name.withTable().as("name"),
                User_Table.portrait.withTable().as("portrait"))
                .from(GroupMember.class)
                .join(User.class, Join.JoinType.INNER)
                .on(GroupMember_Table.user_id.withTable().eq(User_Table.id.withTable()))
                .where(GroupMember_Table.group_id.eq(groupId))
                .orderBy(GroupMember_Table.user_id,true)
                .limit(size)
                .queryCustomList(MemberUserModel.class);
    }
}
