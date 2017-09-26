package com.example.factory.presenter.group;

import android.support.v7.util.DiffUtil;

import com.example.factory.data.group.GroupsDataSource;
import com.example.factory.data.group.GroupsRepository;
import com.example.factory.data.helper.GroupHelper;
import com.example.factory.data.helper.UserHelper;
import com.example.factory.model.db.Group;
import com.example.factory.presenter.BaseSourcePresenter;
import com.example.factory.utils.DiffUiDataCallback;

import java.util.List;

/**
 * 我的群组列表的逻辑
 * Created by Administrator on 2017/9/12.
 */

public class GroupsPresenter extends BaseSourcePresenter<Group,Group,GroupsDataSource,GroupsContract.View>
    implements GroupsContract.Presenter{

    /**
     * 构造函数
     *
     * @param view
     * @param source
     */
    public GroupsPresenter(GroupsContract.View view) {
        super(view, new GroupsRepository());
    }

    @Override
    public void start() {
        super.start();

        // 加载网络数据库数据
        // 只有用户下拉请求网络刷新
        GroupHelper.refreshGroups();
    }

    @Override
    public void onDataLoaded(List<Group> groups) {
        final GroupsContract.View view = getView();
        if(view == null)
            return;
        // 对比差异
        List<Group> olds = view.getRecyclerAdapter().getItems();
        DiffUiDataCallback<Group> callback = new DiffUiDataCallback<>(olds,groups);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        // 刷新界面
        refreshData(result,groups);
    }
}
