package com.example.common.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.Request;
import com.example.common.R;
import com.example.factory.model.Author;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017/7/30.
 */

public class PortraitView extends CircleImageView {
    public PortraitView(Context context) {
        super(context);
    }

    public PortraitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PortraitView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setup(RequestManager requestManager,Author user){
        if(user == null)
            return;
        setup(requestManager,user.getPortrait());
    }

    public void setup(RequestManager requestManager,String url){
        setup(requestManager, R.drawable.default_portrait,url);
    }

    // 占位布局 resourceId
    public void setup(RequestManager requestManager,int resourceId,String url){
        if(url == null)
            url = "";
        requestManager.load(url)
                .placeholder(resourceId)
                .centerCrop()
                .dontAnimate() // CircleImageView 不可以使用动画 使用动画会导致显示延迟
                .into(this);

    }
}
