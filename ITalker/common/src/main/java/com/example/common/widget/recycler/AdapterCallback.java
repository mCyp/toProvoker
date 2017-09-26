package com.example.common.widget.recycler;

/**
 * Created by Administrator on 2017/7/20.
 */

public interface AdapterCallback<Data> {
        void update(Data data,RecyclerAdapter.ViewHolder<Data> holder);
}
