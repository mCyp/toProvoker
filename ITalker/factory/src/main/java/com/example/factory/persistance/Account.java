package com.example.factory.persistance;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.factory.Factory;
import com.example.factory.model.api.account.AccountRspModel;
import com.example.factory.model.db.User;
import com.example.factory.model.db.User_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * Created by Administrator on 2017/8/15.
 */

public class Account {

    private static final String KEY_PUSH_ID = "KEY_PUSH_ID";
    private static final String KEY_IS_BIND = "KEY_IS_BIND";
    private static final String KEY_TOKEN = "KEy_TOKEN";
    private static final String KEY_USER_ID = "KEY_USER_ID";
    private static final String KEY_ACCOUNT = "KEY_ACCOUNT";
    // 设备的推送Id
    private static String pushId;
    // 是否绑定
    private static boolean isBind;
    // 登录状态下的token
    private static String token;
    //登录状态下的用户ID
    private static String userId;
    // 登录的账户
    private static String account;

    private static void save(Context context){
        // 获取数据持久化的sp
        SharedPreferences sp = context.getSharedPreferences(Account.class.getName(),
                Context.MODE_PRIVATE);
        // 储存数据
        sp.edit()
                .putString(KEY_PUSH_ID,pushId)
                .putBoolean(KEY_IS_BIND,isBind)
                .putString(KEY_TOKEN, token)
                .putString(KEY_USER_ID, userId)
                .putString(KEY_ACCOUNT, account)
                .apply();
    }

    /**
     * 数据加载
     * @param context Context
     */
    public static void load(Context context){
        // 获取数据持久化的sp
        SharedPreferences sp = context.getSharedPreferences(Account.class.getName(),
                Context.MODE_PRIVATE);
        pushId = sp.getString(KEY_PUSH_ID,"");
        isBind = sp.getBoolean(KEY_IS_BIND,false);
        token = sp.getString(KEY_TOKEN, "");
        userId = sp.getString(KEY_USER_ID, "");
        account = sp.getString(KEY_ACCOUNT, "");
    }

    /**
     * 获取推送的id
     * @return
     */
    public static String getPushId(){
        return pushId;
    }

    /**
     * 设置并存储pushId
     * @param pushId
     */
    public static void setPushId(String pushId){
        Account.pushId = pushId;
        Account.save(Factory.app());
    }

    /**
     * 判断账号是否登陆 默认为登陆
     * @return boolean
     */
    public static boolean isLogin(){
        // 用户id 和 token 不为空
        return !TextUtils.isEmpty(token)
                && !TextUtils.isEmpty(userId);
    }

    /**
     * 是否已经完善
     * @return
     */
    public static boolean isComplete(){
        if(isLogin()){
            // 检测用户信息是否完全
            User user = getUser();
            return !TextUtils.isEmpty(user.getDesc())
                    && !TextUtils.isEmpty(user.getPortrait())
                    && user.getSex()!= 0;
        }
        // 登录的信息不完全
        return false;
    }

    /**
     * 是否已经绑定到了服务器
     * @return
     */
    public static boolean isBind(){
        return isBind;
    }


    /**
     * 设置bind状态
     * @param isBind
     */
    public static void setBind(boolean isBind){
        Account.isBind = isBind;
        Account.save(Factory.app());
    }

    /**
     * 保存我自己的信息到持久化的XML文件中
     * @param model AccountRspModel
     */
    public static void login(AccountRspModel model){
        Account.token = model.getToken();
        Account.account = model.getAccount();
        Account.userId = model.getUser().getId();
        save(Factory.app());
    }

    /**
     * 获取当前登录的用户信息
     * @return User
     */
    public static User getUser() {
        // 如果为mull 就返回一个new 的User ,否则就去数据库查询一个
        return TextUtils.isEmpty(userId) ? new User() :
                SQLite.select()
                        .from(User.class)
                        .where(User_Table.id.eq(userId))
                        .querySingle();// 查询一个
    }

    /**
     * 获取token
     * @return String
     */
    public static String getToken(){
        return token;
    }

    // 得到用户Id
    public static String getUserId(){
        return getUser().getId();
    }

}
