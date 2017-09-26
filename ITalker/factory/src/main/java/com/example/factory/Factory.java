package com.example.factory;

import android.support.annotation.StringRes;
import android.util.Log;

import com.example.common.app.Application;
import com.example.factory.data.DataSource;
import com.example.factory.data.group.GroupCenter;
import com.example.factory.data.group.GroupDispatcher;
import com.example.factory.data.message.MessageCenter;
import com.example.factory.data.message.MessageDispatcher;
import com.example.factory.data.user.UserCenter;
import com.example.factory.data.user.UserDispatcher;
import com.example.factory.model.api.RspModel;
import com.example.factory.model.api.base.PushModel;
import com.example.factory.model.api.card.GroupCard;
import com.example.factory.model.api.card.GroupMemberCard;
import com.example.factory.model.api.card.MessageCard;
import com.example.factory.model.api.card.UserCard;
import com.example.factory.persistance.Account;
import com.example.factory.utils.DBFlowExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/8/8.
 */

public class Factory {
    private static final String TAG = Factory.class.getSimpleName();
    // 单例模式
    private static final  Factory instance;
    // 全局的线程池
    private final Executor executor;
    // 全局的json
    private final Gson gson;

    static {
        instance  = new Factory();
    }

    private Factory(){
        // 新建一个4个线程的线程池
        executor = Executors.newFixedThreadPool(4);
        gson = new GsonBuilder()
                // 设置日期格式
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                // 设置一个过滤器 数据库级别的model不进行数据转换
                .setExclusionStrategies(new DBFlowExclusionStrategy())
                .create();
    }

    /**
     * Factory 中的初始化
     */
    public static void setup(){
        // 初始化数据库
        FlowManager.init(new FlowConfig.Builder(app())
        .openDatabasesOnInit(true) // 初始化数据的时候就打开
        .build());

        // 持久化的数据进行初始化
        Account.load(app());
    }

    /**
     * 返回全局的Application
     * @return Application
     */
    public static Application app(){
        return Application.getInstance();
    }

    /**
     *  异步运行方法
     * @param runnable Runnable
     */
    public static void runOnAsync(Runnable runnable){
        // 拿到单例 ，  拿到线程池 ， 然后异步执行
        instance.executor.execute(runnable);
    }

    /**
     * 返回一个全局的json，在这可以进行json的全局的初始化
     * @return
     */
    public static Gson getGson(){
        return instance.gson;
    }

    /**
     * 进行错误code 解析
     * ，把网络返回的code进行统一的规划返回String 类型
     * @param model
     * @param callback
     */
    public static void decodeRspCode(RspModel model, DataSource.FailedCallback callback){
        if(model == null)
            return;
        // 进行Code区分
        switch (model.getCode()) {
            case RspModel.SUCCEED:
                return;
            case RspModel.ERROR_SERVICE:
                decodeRspCode(R.string.data_rsp_error_service, callback);
                break;
            case RspModel.ERROR_NOT_FOUND_USER:
                decodeRspCode(R.string.data_rsp_error_not_found_user, callback);
                break;
            case RspModel.ERROR_NOT_FOUND_GROUP:
                decodeRspCode(R.string.data_rsp_error_not_found_group, callback);
                break;
            case RspModel.ERROR_NOT_FOUND_GROUP_MEMBER:
                decodeRspCode(R.string.data_rsp_error_not_found_group_member, callback);
                break;
            case RspModel.ERROR_CREATE_USER:
                decodeRspCode(R.string.data_rsp_error_create_user, callback);
                break;
            case RspModel.ERROR_CREATE_GROUP:
                decodeRspCode(R.string.data_rsp_error_create_group, callback);
                break;
            case RspModel.ERROR_CREATE_MESSAGE:
                decodeRspCode(R.string.data_rsp_error_create_message, callback);
                break;
            case RspModel.ERROR_PARAMETERS:
                decodeRspCode(R.string.data_rsp_error_parameters, callback);
                break;
            case RspModel.ERROR_PARAMETERS_EXIST_ACCOUNT:
                decodeRspCode(R.string.data_rsp_error_parameters_exist_account, callback);
                break;
            case RspModel.ERROR_PARAMETERS_EXIST_NAME:
                decodeRspCode(R.string.data_rsp_error_parameters_exist_name, callback);
                break;
            case RspModel.ERROR_ACCOUNT_TOKEN:
                Application.showToast(R.string.data_rsp_error_account_token);
                instance.logout();
                break;
            case RspModel.ERROR_ACCOUNT_LOGIN:
                decodeRspCode(R.string.data_rsp_error_account_login, callback);
                break;
            case RspModel.ERROR_ACCOUNT_REGISTER:
                decodeRspCode(R.string.data_rsp_error_account_register, callback);
                break;
            case RspModel.ERROR_ACCOUNT_NO_PERMISSION:
                decodeRspCode(R.string.data_rsp_error_account_no_permission, callback);
                break;
            case RspModel.ERROR_UNKNOWN:
            default:
                decodeRspCode(R.string.data_rsp_error_unknown, callback);
                break;
        }

    }

    private static void decodeRspCode(@StringRes int resId,DataSource.FailedCallback callback){
        if(callback != null)
            callback.onDataNotAvailable(resId);
    }

    /**
     * 收到账户退出的消息需要进行账户退出重新登录
     */
    private void logout() {

    }

    /**
     * 处理发送来的消息
     */
    public static void dispatchPush(String str){
        // 判断是否在登录的情况下
        if (!Account.isLogin())
            return;

        PushModel model = PushModel.decode(str);
        if(model == null)
            return;

        // 对推送集合进行遍历
        for (PushModel.Entity entity : model.getEntities()) {
            Log.e(TAG,"dispatchPush-entity"+entity.toString());
            switch (entity.type){
                case PushModel.ENTITY_TYPE_LOGOUT:
                    instance.logout();
                    // 退出情况下，直接返回，并且不可继续
                    return;
                case PushModel.ENTITY_TYPE_MESSAGE:{
                    // 普通消息
                    MessageCard card = getGson().fromJson(entity.content,MessageCard.class);
                    getMessageCenter().dispatch(card);
                    break;
                }
                case PushModel.ENTITY_TYPE_ADD_FRIEND:{
                    // 添加好友的消息
                    UserCard card = getGson().fromJson(entity.content,UserCard.class);
                    getUserCenter().dispatch(card);
                    break;
                }
                case PushModel.ENTITY_TYPE_ADD_GROUP:{
                    // 添加群组的消息
                    GroupCard card = getGson().fromJson(entity.content,GroupCard.class);
                    getGroupCenter().dispatch(card);
                    break;
                }
                case PushModel.ENTITY_TYPE_MODIFY_GROUP_MEMBERS:
                case PushModel.ENTITY_TYPE_ADD_GROUP_MEMBERS:{
                    // 群成员添加和群成员变更
                    Type type = new TypeToken<List<GroupMemberCard>>(){}.getType();
                    List<GroupMemberCard> cards =getGson().fromJson(entity.content,type);
                    // 把数据集合丢到数据中心去处理
                    getGroupCenter().dispatch(cards.toArray(new GroupMemberCard[0]));
                    break;
                }
                case PushModel.ENTITY_TYPE_EXIT_GROUP_MEMBERS:
                    // TODO 群成员的退出
            }
        }

    }

    /**
     * 获取一个用户中心的实现类
     * @return 用户中心的规范接口
     */
    public static UserCenter getUserCenter(){
        return UserDispatcher.instance();
    }

    /**
     * 获取一个消息中心的实现类
     * @return 消息中心的规范接口
     */
    public static MessageCenter getMessageCenter(){
        return MessageDispatcher.instance();
    }

    /**
     * 获取一个群中心的实现类
     * @return 群中心的规范接口
     */
    public static GroupCenter getGroupCenter(){
        return GroupDispatcher.instance();
    }
}
