package com.example.factory.presenter;



import android.support.v7.util.DiffUtil;

import com.example.common.widget.recycler.RecyclerAdapter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

/**
 * 不用刷新全部界面完成添加的逻辑
 * Created by Administrator on 2017/8/24.
 */

public class BaseRecyclerPresenter<ViewMode,View extends BaseContract.RecyclerView>
    extends BasePresenter<View>{
    /**
     * 构造函数
     *
     * @param view
     */
    public BaseRecyclerPresenter(View view) {
        super(view);
    }

    /**
     * 刷新一堆新数据到界面中
     * @param dataList
     */
    protected void refreshData(final List<ViewMode> dataList){
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                View view = getView();
                if(view == null)
                    return;

                // 基本的更新数据并刷新界面
                RecyclerAdapter adapter = view.getRecyclerAdapter();
                adapter.replace(dataList);
                getView().onAdapterDataChanged();
            }
        });
    }

    /**
     * 刷新界面的操作 该方法可以保证界面在主线程进行刷新
     * @param diffResult 一个差异的结果集
     * @param dataList 具体的新数据
     */
    protected void refreshData(final DiffUtil.DiffResult diffResult, final List<ViewMode> dataList){
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                // 这里是主线程运行时
                refreshDataOnUiThread(diffResult, dataList);
            }
        });
    }

    private void refreshDataOnUiThread(final DiffUtil.DiffResult diffResult, final List<ViewMode> dataList){
        View view = getView();
        if(view == null)
            return;

        // 基本的更新数据并刷新界面
        RecyclerAdapter<ViewMode> adapter = view.getRecyclerAdapter();
        // 改变数据集合不通知界面刷新
        adapter.getItems().clear();
        adapter.getItems().addAll(dataList);
        // 通知界面刷新占位布局
        view.onAdapterDataChanged();

        // 通知界面进行增量更新
        diffResult.dispatchUpdatesTo(adapter);

    }
}
