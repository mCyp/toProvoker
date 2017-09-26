package com.example.administrator.italker.push;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;

import com.example.administrator.italker.push.activities.AccountActivity;
import com.example.administrator.italker.push.activities.MainActivity;
import com.example.administrator.italker.push.frags.assist.PermissionsFragment;
import com.example.common.app.Activity;
import com.example.factory.persistance.Account;

import net.qiujuer.genius.res.Resource;
import net.qiujuer.genius.ui.animation.AnimatorListener;
import net.qiujuer.genius.ui.compat.UiCompat;


public class LaunchActivity extends Activity {

    private ColorDrawable mBgDrawable;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }



    @Override
    protected void initWidget() {
        super.initWidget();
        //找到跟布局
        View root = findViewById(R.id.activity_lanuch);
        // 找到颜色
        int color = UiCompat.getColor(getResources(),R.color.colorPrimary);
        // 创建一个Drawabke
        ColorDrawable drawable = new ColorDrawable(color);
        // 设置给背景
        root.setBackground(drawable);
        mBgDrawable = drawable;
    }

    @Override
    protected void initData() {
        super.initData();
        // 开始动画到50%
        startAnim(0.5f, new Runnable() {
            @Override
            public void run() {
                waitPushReceiverId();
            }
        });
    }


    private void waitPushReceiverId() {

        if (Account.isLogin()) {
            // 登录情况下 判断是否绑定
            // 如果没有绑定 等待广播接收器进行绑定
            if (Account.isBind()) {
                skip();
                return;
            }
        } else {
            // 没有登录的情况下 是不能进行pushId的绑定
            if (!TextUtils.isEmpty(Account.getPushId())) {
                skip();
                return;
            }
        }


        // 循环等待
        getWindow().getDecorView()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        waitPushReceiverId();
                    }
                }, 500);

    }


    /**
     * 把剩下的50%完成
     */
    private void skip(){
        startAnim(1.0f, new Runnable() {
            @Override
            public void run() {
                reallySkip();
            }
        });
    }

    /**
     * 真实的界面跳转
     */
    private void reallySkip(){
        // 权限检测
        if(PermissionsFragment.haveAll(this,getSupportFragmentManager())){
            // 检查跳转到主页还是跳转到登录页
            if(Account.isLogin()){
                MainActivity.show(this);
                finish();
            }else {
                AccountActivity.show(this);
                finish();
            }
        }
    }


    private void startAnim(float endProgress, final Runnable callback){
        // 获取最终的结束的颜色
        int finalColor = Resource.Color.WHITE;
        // 获取当前的进度 使用ARGB的颜色差值器
        ArgbEvaluator evaluator = new ArgbEvaluator();
        int endColor = (int)evaluator.evaluate(endProgress,mBgDrawable.getColor(),finalColor);
        // 构建属性动画
        ValueAnimator animator = ObjectAnimator.ofObject(this,property,evaluator,endColor);
        // 时间间隔为1.5秒
        animator.setDuration(1500);
        animator.setIntValues(mBgDrawable.getColor(),endColor);
        animator.addListener(new AnimatorListener(){

            @Override
            public void onAnimationEnd(Animator animation) {
                // 结束的时候触发
                super.onAnimationEnd(animation);
                callback.run();
            }
        });
        animator.start();


    }

    /**
     * 给当前界面设置或返回值
     */
    private  final Property<LaunchActivity,Object> property = new Property<LaunchActivity, Object>(Object.class,"color") {
        @Override
        public void set(LaunchActivity object, Object value) {
            object.mBgDrawable.setColor((Integer) value);
        }

        @Override
        public Object get(LaunchActivity object) {
            return object.mBgDrawable.getColor();
        }
    };

}
