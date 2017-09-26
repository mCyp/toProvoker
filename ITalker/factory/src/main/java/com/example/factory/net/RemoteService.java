package com.example.factory.net;

import com.example.factory.model.api.RspModel;
import com.example.factory.model.api.account.AccountRspModel;
import com.example.factory.model.api.account.LoginModel;
import com.example.factory.model.api.account.RegisterModel;
import com.example.factory.model.api.card.GroupCard;
import com.example.factory.model.api.card.GroupMemberCard;
import com.example.factory.model.api.card.MessageCard;
import com.example.factory.model.api.card.UserCard;
import com.example.factory.model.api.group.GroupCreateModel;
import com.example.factory.model.api.group.GroupMemberAddModel;
import com.example.factory.model.api.message.MsgCreateModel;
import com.example.factory.model.api.user.UserUpdateModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * 网络请求的所有接口
 * Created by Administrator on 2017/8/14.
 */

public interface RemoteService {
    /**
     * 网络请求的一个注册接口
     * @param model RegisterModel
     * @return AccountRspModel
     */
    @POST("account/register")
    Call<RspModel<AccountRspModel>> accountRegister(@Body RegisterModel model);

    /**
     * 网络请求的登录接口
     * @param model LoginModel
     * @return AccountRspModel
     */
    @POST("account/login")
    Call<RspModel<AccountRspModel>> accountLogin(@Body LoginModel model);

    /**
     * 绑定设备id
     * @param push String
     * @return AccountRspModel
     */
    @POST("account/bind/{pushId}")
    Call<RspModel<AccountRspModel>> accountBind(@Path(encoded = true,value = "pushId")String push);

    /**
     * 用户更新的接口
     * @param model UserUpdateModel
     * @return UserCard
     */
    @PUT("user")
    Call<RspModel<UserCard>> userUpdate(@Body UserUpdateModel model);

    /**
     * 用户搜索的接口
     * @param name 名字
     * @return RspModel<List<UserCard>>
     */
    @GET("user/search/{name}")
    Call<RspModel<List<UserCard>>> userSearch(@Path("name") String name);

    // 关注用户的接口
    @PUT("user/follow/{id}")
    Call<RspModel<UserCard>> userFollow(@Path("id") String userId);

    /**
     * 获取联系人的接口
     */
    @GET("user/contact")
    Call<RspModel<List<UserCard>>> userContacts();

    /**
     * 获取某人信息的接口
     */
    @GET("user/{id}")
    Call<RspModel<UserCard>> userFind(@Path("id")String id);

    /**
     * 发送信息的接口
     */
    @POST("msg")
    Call<RspModel<MessageCard>> msgPush(@Body MsgCreateModel model);

    /**
     * 创建群的网络接口
     * @param model GroupCreateModel
     * @return GroupCard
     */
    @POST("group")
    Call<RspModel<GroupCard>> groupCreate(@Body GroupCreateModel model);

    /**
     * 获取一个群的信息
     * @param groupId 群id
     * @return GroupCard
     */
    @GET("group/{groupId}")
    Call<RspModel<GroupCard>> groupFind(@Path("groupId")String groupId);

    /**
     * 群搜索的接口
     * @param name
     * @return
     */
    @GET("group/search/{name}")
    Call<RspModel<List<GroupCard>>> groupSearch(@Path(value = "name",encoded = true) String name);

    /**
     * 我的群组接口
     * @param date 日期
     * @return RspModel<List<GroupCard>>
     */
    @GET("group/list/{date}")
    Call<RspModel<List<GroupCard>>> groups(@Path(value = "date",encoded = true) String date);

    /**
     * 群成员列表
     * @param groupId 群id
     * @return RspModel<List<GroupMemberCard>>
     */
    @GET("group/{groupId}/member")
    Call<RspModel<List<GroupMemberCard>>> groupMembers(@Path(value = "groupId") String groupId);

    /**
     * 添加群成员
     * @param groupId 群id
     * @param model GroupMemberAddModel
     * @return RspModel<List<GroupMemberCard>>
     */
    @POST("group/{groupId}/member")
    Call<RspModel<List<GroupMemberCard>>> groupMemberAdd(@Path(value = "groupId") String groupId, @Body GroupMemberAddModel model);
}
