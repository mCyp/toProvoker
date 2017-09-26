package com.example.factory.data.group;

import android.text.TextUtils;

import com.example.factory.data.BaseDbRepository;
import com.example.factory.data.DataSource;
import com.example.factory.data.helper.GroupHelper;
import com.example.factory.model.db.Group;
import com.example.factory.model.db.Group_Table;
import com.example.factory.model.db.view.MemberUserModel;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * 我的群组的数据源仓库 是对GroupDataSource的实现
 * Created by Administrator on 2017/9/12.
 */

public class GroupsRepository extends BaseDbRepository<Group>
        implements GroupsDataSource{
    @Override
    protected boolean isRequired(Group group) {
        // 一个群的信息 只有可能是两种情况
        // 一种是你自己创建的群的信息，另一种是你加入的群的信息
        // 无论是哪一种情况 ，你拿到的只是群的信息，并没有群成员的信息
        // 你需要进行成员初始化工作
        if(group.getGroupMemberCount()>0){
            // 以及初始化了的成员信息
            group.holder = buildGroupHolder(group);
        }else{
            // 代初始化化 的成员信息
            group.holder = null;
            GroupHelper.refreshGroupMembers(group);
        }
        // 所有的群我都需要关注
        return true;
    }

    private String buildGroupHolder(Group group) {
        List<MemberUserModel> userModels = group.getLatelyGroupMembers();
        if(userModels == null || userModels.size() == 0){
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (MemberUserModel userModel : userModels) {
            builder.append(TextUtils.isEmpty(userModel.alias)?userModel.name:userModel.alias);
            builder.append(",");
        }
        builder.delete(builder.lastIndexOf(","),builder.length());
        return builder.toString();
    }

    @Override
    public void load(SucceedCallback<List<Group>> callback) {
        super.load(callback);

        SQLite.select()
                .from(Group.class)
                .orderBy(Group_Table.name,true)
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }
}
