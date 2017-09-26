package com.example.factory.presenter.search;

import android.support.annotation.StringRes;

import com.example.factory.data.DataSource;
import com.example.factory.data.helper.GroupHelper;
import com.example.factory.data.helper.UserHelper;
import com.example.factory.model.api.card.GroupCard;
import com.example.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

import retrofit2.Call;

/**
 * 搜索群的逻辑实现
 * Created by Administrator on 2017/8/19.
 */

public class SearchGroupPresenter extends BasePresenter<SearchContract.GroupView>
implements SearchContract.Presenter,DataSource.Callback<List<GroupCard>>{

    private Call searchCall;

    /**
     * 构造函数
     *
     * @param view
     */
    public SearchGroupPresenter(SearchContract.GroupView view) {
        super(view);
    }

    @Override
    public void search(String content) {
        start();

        Call call = searchCall;
        if(call!=null&&call.isCanceled()){
            // 如果有上一次的请求 并且没有取消 就取消当前的请求
            call.cancel();
        }

        searchCall = GroupHelper.search(content,this);
    }


    @Override
    public void onDataNotAvailable(@StringRes final int strRes) {
        // 搜索失败
        final SearchContract.GroupView view = getView();
        if (view != null){
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.showError(strRes);
                }
            });
        }
    }

    @Override
    public void onDataLoaded(final List<GroupCard> groupCards) {
        // 搜索成功
        final SearchContract.GroupView view = getView();
        if (view != null){
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.onSearchDone(groupCards);
                }
            });
        }
    }
}
