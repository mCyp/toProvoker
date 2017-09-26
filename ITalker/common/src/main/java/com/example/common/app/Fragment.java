package com.example.common.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.common.widget.convention.PlaceHolderView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/7/19.
 */

public abstract class Fragment extends android.support.v4.app.Fragment {
    protected View mRoot;
    protected Unbinder mRootUnbinder;
    protected PlaceHolderView mPlaceHolderView;
    // 标志是否是第一次初始化数据
    protected boolean mIsFirstInitData = true;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        //初始化参数的方法
        initargs(getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(mRoot==null) {
            int layId = getContentLayoutId();
            //初始化当前根布局，但是不在创建的时候添加到container里面去
            View root = inflater.inflate(layId, container, false);
            initWidget(root);
            mRoot = root;
        }else{
            if(mRoot.getParent()!=null){
                //把当前root从父控件中移除
                ((ViewGroup)mRoot.getParent()).removeView(mRoot);
            }
        }
        return mRoot;
    }

    /**
     * 初始化相关参数
     */
    protected  void initargs(Bundle bundle){

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(mIsFirstInitData){
            // 触发一次后不会触发
            mIsFirstInitData = false;
            // 触发
            onFirstIniit();
        }
        //当View完成之后初始化数据
        initData();
    }

    /**
     * 得到当前资源文件的Id
     * @return
     */
    protected  abstract int getContentLayoutId();

    /**
     * 初始化控件
     */
    protected void initWidget(View root){
        mRootUnbinder = ButterKnife.bind(this,root);
    }

    /**
     * 初始化数据
     */
    protected  void initData(){

    }

    /**
     * 当首次初始化数据的时候会调用
     */
    protected  void onFirstIniit(){

    }

    /**
     * 返回按键触发时候调用
     * @return 返回true代表我已处理逻辑 activity不用自己finish.
     * 返回false 代表我没有处理,activity走自己的逻辑
     */
    public boolean onBackPressed(){
        return false;
    }

    /**
     * 设置站位布局
     * @param placeHolderView 继承了占位布局的view
     */
    public void setmPlaceHolderView(PlaceHolderView placeHolderView){
        this.mPlaceHolderView = placeHolderView;
    }
}
