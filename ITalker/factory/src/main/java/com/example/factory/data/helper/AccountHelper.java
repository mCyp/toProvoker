package com.example.factory.data.helper;

import android.text.TextUtils;

import com.example.factory.Factory;
import com.example.factory.R;
import com.example.factory.data.DataSource;
import com.example.factory.model.api.RspModel;
import com.example.factory.model.api.account.AccountRspModel;
import com.example.factory.model.api.account.LoginModel;
import com.example.factory.model.api.account.RegisterModel;
import com.example.factory.model.db.User;
import com.example.factory.net.Network;
import com.example.factory.net.RemoteService;
import com.example.factory.persistance.Account;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/8/13.
 */

public class AccountHelper {
    /**
     *  注册的接口，异步的调用
     * @param model 注册的参数模型
     * @param callback 成功与失败的接口会送
     */
    public static void register(RegisterModel model, final DataSource.Callback<User> callback){
        // 调用Retroit对我们的网络接口做代理
        RemoteService service = Network.remote();
        // 得到一个call
        Call<RspModel<AccountRspModel>> call = service.accountRegister(model);
        // 异步请求
        call.enqueue(new AccountRspCallbak(callback));
    }

    /**
     * 登录的接口，异步的调用
     * @param model 登录的参数模型
     * @param callback 成功与失败的接口会送
     */
    public static void login(LoginModel model, final DataSource.Callback<User> callback){
        // 调用Retroit对我们的网络接口做代理
        RemoteService service = Network.remote();
        // 得到一个call
        Call<RspModel<AccountRspModel>> call = service.accountLogin(model);
        // 异步请求
        call.enqueue(new AccountRspCallbak(callback));
    }

    /**
     * 对设备id进行绑定
     * @param callback
     */
    public static void bindPush(final DataSource.Callback<User> callback){
        // 检查是否为空
        String pushId = Account.getPushId();
        if(TextUtils.isEmpty(pushId))
            return;

        // 利用Retrofit接口做代理
        RemoteService service = Network.remote();
        // 得到一个call
        Call<RspModel<AccountRspModel>> call = service.accountBind(pushId);
        call.enqueue(new AccountRspCallbak(callback));
    }


    /**
     * 请求回调部分封装
     */
    private static class AccountRspCallbak implements Callback<RspModel<AccountRspModel>>{
        final  DataSource.Callback<User> callback;

        AccountRspCallbak(DataSource.Callback<User> callback){
            this.callback = callback;
        }

        @Override
        public void onResponse(Call<RspModel<AccountRspModel>> call, Response<RspModel<AccountRspModel>> response) {
            // 请求返回 得到我们的全局model
            // 内部使用的是Gson 解析
            RspModel<AccountRspModel> rspModel = response.body();

            if (rspModel.success()) {
                // 拿到实体
                AccountRspModel accountRspModel = rspModel.getResult();
                // 拿到我的信息
                User user = accountRspModel.getUser();
                // 第一种方法 直接进行存储
                DbHelper.save(User.class,user);
                //user.save();
                    /*
                    // 第二种 通过ModelAdapter 该方法可以保存一个列
                    //FlowManager.getModelAdapter(User.class).save(user);

                    // 第三种 事务中
                    DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
                    definition.beginTransactionAsync(new ITransaction() {
                        @Override
                        public void execute(DatabaseWrapper databaseWrapper) {
                            FlowManager.getModelAdapter(User.class).save(user);
                        }
                    });*/
                // 同步到XML持久化文件中
                Account.login(accountRspModel);

                //进行的是数据库的缓存和绑定
                if (accountRspModel.isBind()) {
                    //设置绑定状态为true
                    Account.setBind(true);
                    // 然后返回
                    if(callback!=null)
                        callback.onDataLoaded(user);
                } else {
                    // 绑定pushId
                    bindPush(callback);
                }

            } else {
                // 错误解析
                Factory.decodeRspCode(rspModel, callback);

            }

        }

        @Override
        public void onFailure(Call<RspModel<AccountRspModel>> call, Throwable t) {
            // 网络请求失败
            if(callback!=null)
                callback.onDataNotAvailable(R.string.data_network_error);
        }
    }

}


