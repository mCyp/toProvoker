package com.example.administrator.italker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.administrator.italker.push.R;
import com.example.administrator.italker.push.frags.message.ChatGroupFragment;
import com.example.administrator.italker.push.frags.message.ChatUserFragment;
import com.example.common.app.Activity;
import com.example.common.app.Fragment;
import com.example.factory.model.Author;
import com.example.factory.model.db.Group;
import com.example.factory.model.db.Message;
import com.example.factory.model.db.Session;

public class MessageActivity extends Activity {
    // 接受者的id
    public static final String KEY_RECEIVER_ID = "KEY_RECEIVER_ID";
    // 是否是群聊
    private static final String KEY_RECEIVER_IS_GROUP = "KEY_RECEIVER_IS_GROUP";

    private String mReceiverId;
    private boolean mIsGroup;

    /**
     * 通过Session发起聊天
     * @param context 上下文
     * @param session session
     */
    public static void show(Context context, Session session){
        // 过滤
        if(session == null||context == null|| TextUtils.isEmpty(session.getId()))
            return;
        Intent intent = new Intent(context,MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID,session.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP,session.getReceiverType() == Message.RECEIVER_TYPE_GROUP);
        context.startActivity(intent);
    }

    /**
     * 显示人的聊天界面
     * @param context 上下文
     * @param author 人的信息
     */
    public static void show(Context context, Author author){
        // 过滤
        if(author == null||context == null|| TextUtils.isEmpty(author.getId()))
            return;
        Intent intent = new Intent(context,MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID,author.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP,false);
        context.startActivity(intent);
    }

    /**
     * 群聊
     * @param context 上下文
     * @param group 群组
     */
    public static void show(Context context, Group group){
        if(group == null||context == null||TextUtils.isEmpty(group.getId()))
            return;
        Intent intent = new Intent(context,MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID,group.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP,true);
        context.startActivity(intent);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mReceiverId = bundle.getString(KEY_RECEIVER_ID);
        mIsGroup = bundle.getBoolean(KEY_RECEIVER_IS_GROUP);
        // mReceiverId不为空代表参数传达正确
        return !TextUtils.isEmpty(mReceiverId);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
        Fragment fragment;
        // 判断是否是群组聊天还是跟人聊天
        if(mIsGroup){
            fragment = new ChatGroupFragment();
        }else {
            fragment = new ChatUserFragment();
        }

        // 从activity传递参数到Fragment中
        Bundle bundle = new Bundle();
        bundle.putString(KEY_RECEIVER_ID,mReceiverId);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.lay_container,fragment)
                .commit();
    }
}
