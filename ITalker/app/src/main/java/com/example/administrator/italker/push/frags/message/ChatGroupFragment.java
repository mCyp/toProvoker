package com.example.administrator.italker.push.frags.message;


import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.example.administrator.italker.push.R;
import com.example.administrator.italker.push.activities.GroupMembersActivity;
import com.example.administrator.italker.push.activities.PersonalActivity;
import com.example.factory.model.db.Group;
import com.example.factory.model.db.view.MemberUserModel;
import com.example.factory.presenter.message.ChatContract;
import com.example.factory.presenter.message.ChatGroupPresenter;

import java.util.List;

import butterknife.BindView;

/**
 * 群聊天界面的实现
 */
public class ChatGroupFragment extends ChatFragment<Group>
implements ChatContract.GroupView{

    @BindView(R.id.im_header)
    ImageView mHeader;

    @BindView(R.id.lay_memmbers)
    LinearLayout mLayMembers;

    @BindView(R.id.txt_member_more)
    TextView mMemberMore;


    public ChatGroupFragment() {
        // Required empty public constructor
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        Glide.with(this)
                .load(R.drawable.default_banner_group)
                .centerCrop()
                .into(new ViewTarget<CollapsingToolbarLayout,GlideDrawable>(mCollapsingLayout) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        // 直接放置资源的话会有压扁的感觉
                        this.view.setContentScrim(resource.getCurrent());
                    }
                });
    }


    @Override
    protected ChatContract.Presenter initPresenter() {
        return new ChatGroupPresenter(this,mReceiverId);
    }

    // 进行高度的综合运算，透明我们的头像和icon
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        super.onOffsetChanged(appBarLayout, verticalOffset);
        View view = mLayMembers;

        if (verticalOffset == 0) {
            view.setVisibility(View.VISIBLE);
            view.setScaleX(1);
            view.setScaleY(1);
            view.setAlpha(1);

        } else {
            // abs 运算
            verticalOffset = Math.abs(verticalOffset);
            final int TotalScrollRange = appBarLayout.getTotalScrollRange();
            if (verticalOffset >= TotalScrollRange) {
                view.setVisibility(View.INVISIBLE);
                view.setScaleX(0);
                view.setScaleY(0);
                view.setAlpha(0);

            } else {
                float progress = 1 - verticalOffset / (float) TotalScrollRange;
                view.setVisibility(View.VISIBLE);
                view.setScaleX(progress);
                view.setScaleY(progress);
                view.setAlpha(progress);

            }
        }
    }


    @Override
    public void onInit(Group group) {
        // 初始化群组的名字
        mCollapsingLayout.setTitle(group.getName());

        Glide.with(this)
                .load(group.getPicture())
                .centerCrop()
                .placeholder(R.drawable.default_banner_group)
                .into(mHeader);
    }

    @Override
    public void showAdminOption(boolean isAdmin) {
        if(isAdmin){
            mToolbar.inflateMenu(R.menu.chat_group);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId() == R.id.action_add){
                        // mReceiverId就是群id
                        GroupMembersActivity.showAdmin(getContext(),mReceiverId);
                        return true;
                    }
                    return false;
                }
            });
        }

    }

    @Override
    public void onInitGroupMembers(List<MemberUserModel> members, long moreCount) {
        if(members == null || members.size() ==0)
            return;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (final MemberUserModel member : members) {
            ImageView p = (ImageView) inflater.inflate(R.layout.lay_chat_group_portrait,mLayMembers,false);
            mLayMembers.addView(p,0);

            Glide.with(this)
                    .load(member.portrait)
                    .placeholder(R.drawable.default_portrait)
                    .centerCrop()
                    .dontAnimate()
                    .into(p);

            // 个人信息界面查看
            p.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PersonalActivity.show(getContext(),member.id);
                }
            });
        }

        // 更多的按钮显示
        if(moreCount>0){
            mMemberMore.setText(String.format("+%s",moreCount));
            mMemberMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupMembersActivity.show(getContext(),mReceiverId);
                }
            });
        }else{
            mMemberMore.setVisibility(View.GONE);
        }

    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.lay_chat_header_group;
    }
}
