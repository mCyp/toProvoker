package com.example.factory.presenter;

import com.example.factory.data.DataSource;
import com.example.factory.data.DbDataSource;

import java.util.List;

/**
 * 基础的仓库源的Presenter
 * Created by Administrator on 2017/8/28.
 */

public abstract class BaseSourcePresenter<Data,ViewModel,Source extends DbDataSource<Data>
        ,View extends BaseContract.RecyclerView> extends BaseRecyclerPresenter<ViewModel,View>
        implements DataSource.SucceedCallback<List<Data>>{

    protected Source mSource;


    /**
     * 构造函数
     *
     * @param view
     */
    public BaseSourcePresenter(View view,Source source) {
        super(view);
        this.mSource = source;
    }

    @Override
    public void start() {
        super.start();
        if (mSource != null)
            mSource.load(this);
    }

    @Override
    public void destroy() {
        super.destroy();
        mSource.dispose();
        mSource = null;
    }
}
