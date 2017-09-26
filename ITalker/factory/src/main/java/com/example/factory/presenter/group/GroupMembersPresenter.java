package com.example.factory.presenter.group;

import com.example.factory.Factory;
import com.example.factory.data.helper.GroupHelper;
import com.example.factory.model.db.view.MemberUserModel;
import com.example.factory.presenter.BaseRecyclerPresenter;

import java.util.List;

/**
 * 显示群成员的逻辑
 * Created by Administrator on 2017/9/14.
 */

public class GroupMembersPresenter extends BaseRecyclerPresenter<MemberUserModel,GroupMembersContract.View>
implements GroupMembersContract.Presenter{

    private Runnable load = new Runnable() {
        @Override
        public void run() {
            GroupMembersContract.View view = getView();
            if(view == null)
                return;
            String groupId = view.getmGroupId();
            // 传递数量为-1的情况下 代表查询所有
            List<MemberUserModel> models = GroupHelper.getMemberUsers(groupId,-1);
            refreshData(models);
        }
    };

    /**
     * 构造函数
     *
     * @param view
     */
    public GroupMembersPresenter(GroupMembersContract.View view) {
        super(view);
    }

    @Override
    public void refresh() {
        // 显示loading
        start();
        // 异步加载
        Factory.runOnAsync(load);
    }
}
