package com.example.common.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.example.common.widget.convention.PlaceHolderView;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/7/19.
 */

public abstract class Activity extends AppCompatActivity {

    protected PlaceHolderView mPlaceHolderView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //在界面未初始化之前调用的初始化窗口
        initWindows();
        if(initArgs(getIntent().getExtras())){
            //得到界面ID并设置到界面中
            int LayId = getContentLayoutId();
            setContentView(LayId);

            initBefore();
            initWidget();
            initData();
        }else{
            finish();
        }
    }

    /**
     * 初始化窗口
     */
    protected void initWindows(){

    }

    /**
     * 初始化之前调用
     */
    protected void initBefore(){

    }

    /**
     * 初始化相关参数
     * @param bundle
     * @return 参数正确返回true 参数错误返回false
     */
    protected  boolean initArgs(Bundle bundle){
        return true;
    }

    /**
     * 得到当前资源文件的Id
     * @return
     */
    protected  abstract int getContentLayoutId();

    /**
     * 初始化控件
     */
    protected void initWidget(){
        ButterKnife.bind(this);
    }

    /**
     * 初始化数据
     */
    protected  void initData(){

    }

    @Override
    public boolean onSupportNavigateUp() {
        //当点击界面导航返回时finish当前界面
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        //得到当前activity下的所有fragment
        @SuppressLint("RestrictedApi")
        List<android.support.v4.app.Fragment> fragments =getSupportFragmentManager().getFragments();
        //判断是否为空
        if(fragments!=null&&fragments.size()>0){
            for(Fragment fragment:fragments){
                //判断是否为我们能够处理的Fragment类型
                if(fragment instanceof com.example.common.app.Fragment){
                    //判断是否拦截了返回按钮
                    if(((com.example.common.app.Fragment) fragment).onBackPressed()){
                        //如果有直接return
                        return;
                    }
                }
            }
        }
        super.onBackPressed();
        finish();
    }

    /**
     * 设置站位布局
     * @param placeHolderView 继承了占位布局的view
     */
    public void setmPlaceHolderView(PlaceHolderView placeHolderView){
        this.mPlaceHolderView = placeHolderView;
    }
}
