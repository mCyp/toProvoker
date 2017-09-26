package com.example.factory.presenter.contact;

import com.example.factory.Factory;
import com.example.factory.data.helper.UserHelper;
import com.example.factory.model.db.User;
import com.example.factory.persistance.Account;
import com.example.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * Created by Administrator on 2017/8/21.
 */

public class PersonalPresenter extends BasePresenter<PersonalContract.View>
        implements PersonalContract.Presenter {
    private User user;

    /**
     * 构造函数
     *
     * @param view
     */
    public PersonalPresenter(PersonalContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
        String id = getView().getUserId();

        // 个人用户数据优先从网络拉去

        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                PersonalContract.View view = getView();
                if(view != null){
                    String id = getView().getUserId();
                    User user = UserHelper.findFromNet(id);
                    onLoaded(user);
                }
            }
        });

    }

    /**
     * 进行界面的设置
     * @param user 用户信息
     */
    private void onLoaded(final User user){
        this.user =user;
        // 是否是用户自己
        final boolean isSelf = user.getId().equalsIgnoreCase(Account.getUserId());
        // 是否允许关注
        final boolean isFollow = isSelf || user.isFollow();
        // 是否可以会话
        final boolean allowSayHello = !isSelf && isFollow;
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                final PersonalContract.View view = getView();
                if(view == null)
                    return;
                getView().onLoadDone(user);
                getView().setFollowStatus(isFollow);
                getView().allowSayHello(allowSayHello);
            }
        });
    }

    @Override
    public User getUserPersonal() {
        return user;
    }
}
