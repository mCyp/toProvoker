package com.example.factory.data.helper;

import com.example.factory.Factory;
import com.example.factory.R;
import com.example.factory.data.DataSource;
import com.example.factory.model.api.RspModel;
import com.example.factory.model.api.card.UserCard;
import com.example.factory.model.api.user.UserUpdateModel;
import com.example.factory.model.db.User;
import com.example.factory.model.db.view.UserSampleMode;
import com.example.factory.model.db.User_Table;
import com.example.factory.net.Network;
import com.example.factory.net.RemoteService;
import com.example.factory.persistance.Account;
import com.example.utils.CollectionUtil;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 更新的网络请求
 * Created by Administrator on 2017/8/18.
 */

public class UserHelper {
    public static void update(UserUpdateModel model, final DataSource.Callback<UserCard> callback){
        // 调用Retroit对我们的网络接口做代理
        RemoteService service = Network.remote();
        // 得到一个call
        Call<RspModel<UserCard>> call = service.userUpdate(model);

        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                // 请求返回 得到我们的全局model
                // 内部使用的是Gson 解析
                RspModel<UserCard> rspModel = response.body();
                if(rspModel.success()){
                    UserCard userCard = rspModel.getResult();
                    // 数据库的存储操作 需要吧UserCard 转化为user
                    // 唤起进行保存的操作
                    Factory.getUserCenter().dispatch(userCard);
                    callback.onDataLoaded(userCard);

                }else{
                    Factory.decodeRspCode(rspModel,callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                // 网络请求失败
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    public static Call search(String  name, final DataSource.Callback<List<UserCard>> callback){
        RemoteService service = Network.remote();
        Call<RspModel<List<UserCard>>> call = service.userSearch(name);

        call.enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                // 请求返回 得到我们的全局model
                // 内部使用的是Gson 解析
                RspModel<List<UserCard>> rspModel = response.body();
                if(rspModel.success()){
                    callback.onDataLoaded(rspModel.getResult());
                }else {
                    Factory.decodeRspCode(rspModel,callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                // 网络请求失败
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });

        // 返回当前的call 防止用户多次点击 前面的数据还没有回来
        return call;

    }

    // 刷新联系人的操作，不需要callback 直接存储到数据库
    // 并通过数据库观察者进行更新
    // 界面更新的时候然后对比，进行差异更新。
    public static void refreshContacts(){
        RemoteService service = Network.remote();
        service.userContacts().enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                // 请求返回 得到我们的全局model
                // 内部使用的是Gson 解析
                RspModel<List<UserCard>> rspModel = response.body();
                if(rspModel.success()){
                    // 拿到集合
                    List<UserCard> cards = rspModel.getResult();
                    if(cards == null || cards.size()==0)
                        return;
                    UserCard[] userCards = CollectionUtil.toArray(cards, UserCard.class);
                    Factory.getUserCenter().dispatch(userCards);
                }else {
                    Factory.decodeRspCode(rspModel,null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                // 什么也不做
            }
        });

    }

    // 关注的方法
    public static void follow(String id, final DataSource.Callback<UserCard> callback) {
        RemoteService service = Network.remote();
        Call<RspModel<UserCard>> call = service.userFollow(id);

        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                // 请求返回 得到我们的全局model
                // 内部使用的是Gson 解析
                RspModel<UserCard> rspModel = response.body();
                if(rspModel.success()){
                    UserCard userCard = rspModel.getResult();

                    // 唤起进行保存的操作
                    Factory.getUserCenter().dispatch(userCard);
                    callback.onDataLoaded(userCard);
                }else {
                    Factory.decodeRspCode(rspModel,callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                // 网络请求失败
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    // 从本地查询一个用户的信息
    public static User findFromLocal(String id){
        return SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(id))
                .querySingle();
    }

    // 从网络查询一个用户的信息
    public static User findFromNet(String id){
        RemoteService remoteService = Network.remote();
        try {
            Response<RspModel<UserCard>> response = remoteService.userFind(id).execute();
            UserCard userCard = response.body().getResult();
            if(userCard != null){
                User user =userCard.build();
                // 唤起进行保存的操作
                Factory.getUserCenter().dispatch(userCard);
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取一个User 优先从本地缓存拉取
     * @param id 用户id
     * @return User
     */
    public static User search(String id){
        User user =findFromLocal(id);
        if(user == null){
            return  user =findFromNet(id);
        }
        return user;
    }

    /**
     * 获取一个User 优先从网络拉取
     * @param id 用户id
     * @return User
     */
    public static User searchFirstOfNet(String id){
        User user =findFromNet(id);
        if(user == null){
            return  user =findFromLocal(id);
        }
        return user;
    }

    /**
     * 获取联系人
     */
    public static List<User> getContact(){
        //加载本地数据库数据
        return SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name,true)
                .limit(100)
                .queryList();
    }

    /**
     * 获取一个列表但是是一个简单的数据库级别的
     * @return
     */
    public static List<UserSampleMode> getSampleContact(){
        //加载本地数据库数据
        return SQLite.select(User_Table.id.withTable().as("id"),
                    User_Table.name.withTable().as("name"),
                    User_Table.portrait.withTable().as("portrait"))
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name,true)
                .queryCustomList(UserSampleMode.class);
    }
}
