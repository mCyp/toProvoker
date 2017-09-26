package com.example.administrator.italker.push.frags.main;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.italker.push.R;
import com.example.administrator.italker.push.activities.MessageActivity;
import com.example.common.app.Fragment;
import com.example.common.app.PresenterFragment;
import com.example.common.widget.EmptyView;
import com.example.common.widget.PortraitView;
import com.example.common.widget.recycler.RecyclerAdapter;
import com.example.factory.model.db.Group;
import com.example.factory.presenter.group.GroupsContract;
import com.example.factory.presenter.group.GroupsPresenter;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends PresenterFragment<GroupsContract.Presenter>
    implements GroupsContract.View{

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    // 适配器User 可以直接从数据库查询
    private RecyclerAdapter<Group> mAdapter;


    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        // 初始化mRecyclerView
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        mRecyclerView.setAdapter(mAdapter = new RecyclerAdapter<Group>() {
            @Override
            protected int getItemViewType(int position, Group group) {
                return R.layout.cell_group_list;
            }

            @Override
            protected ViewHolder<Group> onCreateViewHolder(View root, int viewType) {
                return new GroupFragment.ViewHolder(root);
            }
        });

        // 点击的事件监听
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Group>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Group group) {
                // 跳转到聊天界面
                MessageActivity.show(getContext(),group);
            }
        });

        // 为EmptyView 绑定View
        mEmptyView.bind(mRecyclerView);
        setmPlaceHolderView(mEmptyView);
    }

    @Override
    protected void onFirstIniit() {
        super.onFirstIniit();
        // 进行一次数据加载
        mPresenter.start();
    }



    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_group;
    }

    @Override
    protected GroupsContract.Presenter initPresenter() {
        return new GroupsPresenter(this);
    }

    @Override
    public RecyclerAdapter<Group> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        // mAdapter里面的数大于零就显示 不然就不显示
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount()>0);
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<Group>{

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.txt_desc)
        TextView mDesc;

        @BindView(R.id.txt_member)
        TextView mMembers;


        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Group group) {
            // 设置头像
            mPortraitView.setup(Glide.with(GroupFragment.this),group.getPicture());
            mName.setText(group.getName());
            mDesc.setText(group.getDesc());

            if(group.holder != null && group.holder instanceof String){
                mMembers.setText((String)group.holder);
            }else {
                mMembers.setText("");
            }
        }

    }

}
