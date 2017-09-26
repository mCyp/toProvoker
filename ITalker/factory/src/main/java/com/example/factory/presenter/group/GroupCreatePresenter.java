package com.example.factory.presenter.group;

import android.text.TextUtils;

import com.example.factory.Factory;
import com.example.factory.R;
import com.example.factory.data.DataSource;
import com.example.factory.data.helper.GroupHelper;
import com.example.factory.data.helper.UserHelper;
import com.example.factory.model.api.card.GroupCard;
import com.example.factory.model.api.group.GroupCreateModel;
import com.example.factory.model.db.view.UserSampleMode;
import com.example.factory.net.UploadHelper;
import com.example.factory.presenter.BaseRecyclerPresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 群组创建的逻辑
 * Created by Administrator on 2017/9/7.
 */

public class GroupCreatePresenter extends BaseRecyclerPresenter<GroupCreateContract.ViewMode, GroupCreateContract.View>
        implements GroupCreateContract.Presenter,DataSource.Callback<GroupCard>{

    Set<String> users = new HashSet<>();

    private Runnable load = new Runnable() {
        @Override
        public void run() {
            List<UserSampleMode> sampleModels = UserHelper.getSampleContact();
            List<GroupCreateContract.ViewMode> models = new ArrayList<>();
            for (UserSampleMode sampleModel : sampleModels) {
                GroupCreateContract.ViewMode viewMode = new GroupCreateContract.ViewMode();
                viewMode.author = sampleModel;
                models.add(viewMode);
            }
            refreshData(models);
        }
    };

    /**
     * 构造函数
     *
     * @param view
     */
    public GroupCreatePresenter(GroupCreateContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();

        Factory.runOnAsync(load);
    }

    @Override
    public void create(final String name, final String desc, final String picture) {
        GroupCreateContract.View view = getView();
        view.showLoading();

        // 判断参数
        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(desc)||TextUtils.isEmpty(picture)
                ||users.size() == 0){
            view.showError(R.string.label_group_create_invalid);
            return;
        }

        // 上传图片
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                String url = upLoadPicture(picture);
                if(TextUtils.isEmpty(url))
                    return;

                // 创建网络请求
                GroupCreateModel model = new GroupCreateModel(name,desc,url,users);
                GroupHelper.createGroup(model,GroupCreatePresenter.this);
            }
        });
        // 请求接口

        // 处理回调

        //
    }

    @Override
    public void changeSelect(GroupCreateContract.ViewMode mode, boolean isSelected) {
        if (isSelected) {
            users.add(mode.author.getId());
        } else {
            users.remove(mode.author.getId());
        }
    }

    // 上传图片
    private String upLoadPicture(String path){
        String url = UploadHelper.uploadPortrait(path);
        if(TextUtils.isEmpty(url)){
            // 切换到主线程 提示信息
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    GroupCreateContract.View view = getView();
                    if(view != null){
                        view.showError(R.string.data_upload_error);
                    }
                }
            });
        }
        return url;
    }

    @Override
    public void onDataLoaded(GroupCard groupCard) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                GroupCreateContract.View view = getView();
                if(view != null){
                    view.onCreateSucceed();
                }
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                GroupCreateContract.View view = getView();
                if(view != null){
                    view.showError(strRes);
                }
            }
        });
    }
}
