package com.example.factory.presenter.search;

import com.example.factory.model.api.card.GroupCard;
import com.example.factory.model.api.card.UserCard;
import com.example.factory.presenter.BaseContract;

import java.util.List;

/**
 * Created by Administrator on 2017/8/19.
 */

public interface SearchContract {
    interface Presenter extends BaseContract.Presenter{
        void search(String content);
    }

    // 搜索人的界面
    interface UserView extends BaseContract.View<Presenter>{
        void onSearchDone(List<UserCard> userCards);
    }

    // 搜索群的界面
    interface GroupView extends BaseContract.View<Presenter>{
        void onSearchDone(List<GroupCard> grouoCards);
    }
}
