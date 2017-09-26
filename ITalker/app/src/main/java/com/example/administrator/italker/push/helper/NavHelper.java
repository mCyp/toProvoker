package com.example.administrator.italker.push.helper;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.ScrollingTabContainerView;
import android.util.SparseArray;


/**
 * 完成对fragment的调度和重用问题，达到最优的Fragment切换
 * Created by Administrator on 2017/8/1.
 */

public class NavHelper<T> {
    //所有的Tab集合
    private final SparseArray<Tab<T>> tabs = new SparseArray();

    //用于初始化的必须参数
    private final Context context;
    private final int containerId;
    private final FragmentManager fragmentManager;
    private final onTabChangerListener<T> listener;

    //当前的一个选中tab
    private Tab<T> currentTab;

    public NavHelper(Context context, int containerId, FragmentManager fragmentManager, onTabChangerListener<T> listener) {
        this.context = context;
        this.containerId = containerId;
        this.fragmentManager = fragmentManager;
        this.listener = listener;
    }

    /**
     * 添加Tab
     * @param menuId Tab对应的菜单Id
     * @param tab Tab
     */
    public NavHelper<T> add(int menuId,Tab<T> tab){
        tabs.put(menuId,tab);
        return this;
    }

    /**
     * 获取当前显示Tab
     * @return 当前的Tab
     */
    public Tab<T> getCurrentTab(){
        return currentTab;
    }

    /**
     * 执行点击菜单的操作
     * @param menuId 菜单的Id
     * @return 是否能够处理这个点击
     */
    public boolean performClickMenu(int menuId){
        // 集合中寻找点击的菜单对应的Tab
        // 如果有则进行处理
        Tab<T> tab = tabs.get(menuId);
        if(tab!=null){
            doSelect(tab);
            return true;
        }

        return false;
    }

    /**
     *  进行真实的Tab选择操作
     * @param tab
     */
    private void doSelect(Tab<T> tab){
        Tab<T> oldTab = null;

        if(currentTab !=null){
            oldTab=currentTab;
            if(oldTab==tab){
                // 如果说当前的Tab就是点击的Tab
                // 那么我们不做处理
                notifyTabReselect(tab);
                return;
            }
        }
        //赋值并调用切换方法
        currentTab = tab;
        doTabChanged(currentTab,oldTab);

    }

    /**
     * 进行Fragment的真实的调度操作
     * @param newTab 新的
     * @param oloTab 旧的
     */
    private void doTabChanged(Tab<T> newTab,Tab<T> oloTab){
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if(oloTab!=null){
            if(oloTab.fragment!=null){
                //从界面移除，但是还在Fragment的缓存空间中
                ft.detach(oloTab.fragment);
            }
        }

        if(newTab!=null){
            if(newTab.fragment==null){
                //首次新建
                Fragment fragment = Fragment.instantiate(context,newTab.clx.getName(),null);
                //缓存起来
                newTab.fragment = fragment;
                //提交到FragmentManager
                ft.add(containerId,fragment,newTab.clx.getName());
            }else{
                // 从提交到FragmentManager的缓存空间中重新加载到界面中
                ft.attach(newTab.fragment);
            }
        }
        //提交事物
        ft.commit();
        //通知回调
        notifyTabSelect(newTab,oloTab);
    }

    /**
     * 回调我们的监听器
     * @param newTab 新的Tab
     * @param oldTab 旧的Tab
     */
    private void notifyTabSelect(Tab<T> newTab,Tab<T> oldTab){
        if(listener!=null){
            listener.onTabChanged(newTab, oldTab);
        }
    }

    private void notifyTabReselect(Tab<T> tab){
        // TODO 二次点击Tab所做的操作
    }

    /**
     * 我们所有的Tab基础属性
     * @param <T> 泛型的额外参数
     */
    public static  class  Tab<T>{
        public Tab(Class<?> clx, T extra) {
            this.clx = clx;
            this.extra = extra;
        }

        //继承自Fragment用来获取它的信息
        public Class<?> clx;
        //额外的字段，用户自设定需要使用
        public T extra;

        //内部缓存的对应的Fragment
        //package权限，外部无法使用
        Fragment fragment;
    }

    /**
     * 定义事件处理完成后的回调接口
     * @param <T>
     */
    public interface onTabChangerListener<T>{
        void onTabChanged(Tab<T> newTab,Tab<T> oldTab);
    }

}
