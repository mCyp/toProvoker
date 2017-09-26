package com.example.administrator.italker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.example.administrator.italker.push.R;
import com.example.administrator.italker.push.frags.main.ActiveFragment;
import com.example.administrator.italker.push.frags.main.ContactFragment;
import com.example.administrator.italker.push.frags.main.GroupFragment;
import com.example.administrator.italker.push.helper.NavHelper;
import com.example.common.app.Activity;
import com.example.common.widget.PortraitView;
import com.example.factory.persistance.Account;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.widget.FloatActionButton;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends Activity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavHelper.onTabChangerListener<Integer>{
    @BindView(R.id.appbar)
    View mLayAppbar;

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    @BindView(R.id.txt_title)
    TextView mTitle;

    @BindView(R.id.lay_container)
    FrameLayout mContainer;

    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;

    @BindView(R.id.btn_action)
    FloatActionButton mAction;

    private NavHelper<Integer> mNavHelper;

    /**
     * MainActivity 显示的入口
     * @param context 上下文
     */
    public static void show(Context context){
        //
        context.startActivity(new Intent(context,MainActivity.class));
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        if(Account.isComplete()){
            // 登录信息完全 完全就走正常流程
            return super.initArgs(bundle);
        }else{
            UserActivity.show(this);
            return false;
        }
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        //初始化底部辅助工具类
        mNavHelper=new NavHelper<>(this,R.id.lay_container,
                getSupportFragmentManager(),this);
        mNavHelper.add(R.id.action_home,new NavHelper.Tab<>(ActiveFragment.class,R.string.title_home))
            .add(R.id.action_group,new NavHelper.Tab<>(GroupFragment.class,R.string.title_group))
            .add(R.id.action_contact,new NavHelper.Tab<>(ContactFragment.class,R.string.title_contact));

        //添加对底部导航的监听
        mNavigation.setOnNavigationItemSelectedListener(this);
        Glide.with(this).load(R.drawable.bg_src_morning).centerCrop().into(new ViewTarget<View,GlideDrawable>(mLayAppbar) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                this.view.setBackground(resource.getCurrent());
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();

        // 从底部导中接管我们的menu,然后进行手动的触发第一次点击
        Menu menu = mNavigation.getMenu();
        // 触发首次选中Home
        menu.performIdentifierAction(R.id.action_home,0);
        // 初始化我们的头像信息
        mPortrait.setup(Glide.with(this),Account.getUser());
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick(){
        PersonalActivity.show(this,Account.getUserId());
    }

    @OnClick(R.id.im_search)
    void onSearchMenuClick(){
        // 浮动按钮点击的时候 判断是人的界面还是群组的界面
        // 群组界面就搜索群组 用户界面就搜索用户
        int type = Objects.equals(mNavHelper.getCurrentTab().extra,R.string.title_group)? SearchActivity.TYPE_GROUP:SearchActivity.TYPE_GROUP;
        SearchActivity.show(this,type);
    }

    @OnClick(R.id.btn_action)
    void onActionClick(){
        // 浮动按钮点击的时候 判断是人的界面还是群组的界面
        // 群组界面就添加群组 用户界面就添加用户
        if(Objects.equals(mNavHelper.getCurrentTab().extra,R.string.title_group)){
            // 打开群创建界面
            GroupCreateActivity.show(this);
        }else{
            SearchActivity.show(this,SearchActivity.TYPE_USER);
        }
    }


    /**
     * 当我们底部导航被点击触发的时候
     * @param item
     * @return true 代表我们能够处理这个点击
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //转接事件流到工具类中
        return mNavHelper.performClickMenu(item.getItemId());
    }

    /**
     *  NavHelper 处理后回调的方法
     * @param newTab 新的Tab
     * @param oldTab 旧的Tab
     */
    @Override
    public void onTabChanged(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {
        //从额外字段中取出我们的Title资源Id
        mTitle.setText(newTab.extra);

        //对浮动按钮进行隐藏与显示的动画
        float transY =0 ;
        float rotation = 0;
        if(Objects.equals(newTab.extra,R.string.title_home)){
            //主界面时隐藏
            transY = Ui.dipToPx(getResources(),76);
        }else {
            //transY 默认为0则显示
            if(Objects.equals(newTab.extra,R.string.title_group)){
                // 群
                mAction.setImageResource(R.drawable.ic_group_add);
                rotation = -360;
            }else{
                // 联系人
                mAction.setImageResource(R.drawable.ic_contact_add);
                rotation = 360;
            }
        }

        //开始动画
        //旋转，Y轴位移，弹性差值器，时间
        mAction.animate()
                .rotation(rotation)
                .translationY(transY)
                .setInterpolator(new AnticipateOvershootInterpolator(1))
                .setDuration(480)
                .start();
    }
}
