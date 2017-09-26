package com.example.administrator.italker.push.frags.search;


import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.italker.push.R;
import com.example.administrator.italker.push.activities.PersonalActivity;
import com.example.administrator.italker.push.activities.SearchActivity;
import com.example.common.app.PresenterFragment;
import com.example.common.widget.EmptyView;
import com.example.common.widget.PortraitView;
import com.example.common.widget.recycler.RecyclerAdapter;
import com.example.factory.model.api.card.UserCard;
import com.example.factory.presenter.contact.FollowContract;
import com.example.factory.presenter.contact.FollowPresenter;
import com.example.factory.presenter.search.SearchContract;
import com.example.factory.presenter.search.SearchUserPresenter;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.drawable.LoadingCircleDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 搜索用户的界面的实现.
 */
public class SearchUserFragment extends PresenterFragment<SearchContract.Presenter>
        implements SearchActivity.SearchFragment,SearchContract.UserView {

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    private RecyclerAdapter<UserCard> mAdapter;

    public SearchUserFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_user;
    }

    @Override
    protected void initData() {
        super.initData();

        search("");
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        // 初始化mRecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter = new RecyclerAdapter<UserCard>() {
            @Override
            protected int getItemViewType(int position, UserCard userCard) {
                return R.layout.cell_search_list;
            }

            @Override
            protected ViewHolder<UserCard> onCreateViewHolder(View root, int viewType) {
                return new SearchUserFragment.ViewHolder(root);
            }
        });
        // 为EmptyView 绑定View
        mEmptyView.bind(mRecyclerView);
        setmPlaceHolderView(mEmptyView);
    }

    @Override
    public void onSearchDone(List<UserCard> userCards) {
        // 数据成功的情况下返回数据
        mAdapter.replace(userCards);
        // 有数据就显示OK 没有数据就显示EmptyView
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount()>0);
    }

    @Override
    protected SearchContract.Presenter initPresenter() {
        // 初始化Presenter
        return new SearchUserPresenter(this);
    }

    @Override
    public void search(String content) {
        mPresenter.search(content);
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<UserCard>
            implements FollowContract.View {
        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.im_follow)
        ImageView mFolow;

        @BindView(R.id.txt_name)
        TextView mTextView;

        private FollowContract.Presenter mPresenter;

        public ViewHolder(View itemView) {
            super(itemView);
            new FollowPresenter(this);
        }

        @Override
        protected void onBind(UserCard userCard) {
            // 把userCard里面的头像给传过来
            mPortraitView.setup(Glide.with(SearchUserFragment.this),userCard);

            mTextView.setText(userCard.getName());

            mFolow.setEnabled(!userCard.isFollow());
        }

        @OnClick(R.id.im_follow)
        void onFollowClick(){
            // 发起关注
            mPresenter.follow(mData.getId());
        }

        @OnClick(R.id.im_portrait)
        void onPortraitClick(){
            // 显示信息的入口
            PersonalActivity.show(getContext(),mData.getId());
        }


        @Override
        public void showError(@StringRes int str) {
            // 更改当前界面的状态
            if(mFolow.getDrawable() instanceof LoadingDrawable){
                // 失败则停止一个动画  则显示一个圆圈
                LoadingDrawable loadingDrawable = (LoadingDrawable) mFolow.getDrawable();
                loadingDrawable.setProgress(1);
                loadingDrawable.stop();
            }
        }

        @Override
        public void showLoading() {
            int minSize = (int) Ui.dipToPx(getResources(),22);
            int maxSize = (int) Ui.dipToPx(getResources(),30);
            // 初始化一个圆形的drawable
            LoadingDrawable drawable = new LoadingCircleDrawable(minSize,maxSize);
            drawable.setBackgroundColor(0);

            int[] color = new int[]{UiCompat.getColor(getResources(),R.color.white_alpha_208)};
            drawable.setForegroundColor(color);

            mFolow.setImageDrawable(drawable);
            drawable.start();
        }

        @Override
        public void setPresenter(FollowContract.Presenter presenter) {
            mPresenter = presenter;
        }

        @Override
        public void onFollowSucceed(UserCard userCard) {
            // 更改当前界面的状态
            if(mFolow.getDrawable() instanceof LoadingDrawable){
                ((LoadingDrawable) mFolow.getDrawable()).stop();
                mFolow.setImageResource(R.drawable.sel_opt_done_add);
            }
            // 更新
            updateData(userCard);
        }
    }



}
