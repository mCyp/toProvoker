package com.example.factory.data.helper;

import android.os.SystemClock;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.common.Common;
import com.example.common.app.Application;
import com.example.factory.Factory;
import com.example.factory.model.api.RspModel;
import com.example.factory.model.api.card.MessageCard;
import com.example.factory.model.api.message.MsgCreateModel;
import com.example.factory.model.db.Message;
import com.example.factory.model.db.Message_Table;
import com.example.factory.net.Network;
import com.example.factory.net.RemoteService;
import com.example.factory.net.UploadHelper;
import com.example.utils.PicturesCompressor;
import com.example.utils.StreamUtil;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.File;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/8/23.
 */

public class MessageHelper {

    // 从本地选取一条消息
    public static Message findFromLocal(String id) {
        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.id.eq(id))
                .querySingle();
    }

    // 发送是异步进行的
    public static void push(final MsgCreateModel model) {
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                // 如果是一个已经发送过的消息 则不能重新发送
                Message message = MessageHelper.findFromLocal(model.getId());
                if(message!=null && message.getStatus() != Message.STATUS_FAILED)
                    return;

                // 我们在发送信息的时候需要通知界面更新状态
                final MessageCard card = model.buildCard();
                // 通知数据库刷新
                Factory.getMessageCenter().dispatch(card);

                // 如果是文件类型的消息 则需要先上传 再继续发送
                if(card.getType() != Message.TYPE_STR){
                    // 不是文件类型
                    if(!card.getContent().startsWith(UploadHelper.ENDPOINT)){
                        // 没有上传到云服务器
                        String content;
                        switch (card.getType()){
                            case Message.TYPE_PIC:
                                content = upLoadPicture(card.getContent());
                                break;
                            case Message.TYPE_AUDIO:
                                content = upLoadAudio(card.getContent());
                                break;
                            case Message.TYPE_FILE:
                                content = "";
                                break;
                            default:
                                content = "";
                                break;
                        }
                        if(TextUtils.isEmpty(content)){
                            // 上传失败
                            card.setStatus(Message.STATUS_FAILED);
                            Factory.getMessageCenter().dispatch(card);
                            return;
                        }
                        // 成功的话则把路径返回
                        card.setContent(content);
                        Factory.getMessageCenter().dispatch(card);
                        // 因为卡片的内容改变了 而我们上传服务器使用的model
                        // 所以model也跟着改变了
                        model.refreshByCard();
                    }
                }

                //如果是普通类型的消息 就直接发送 进行网络调度
                RemoteService service = Network.remote();
                service.msgPush(model).enqueue(new Callback<RspModel<MessageCard>>() {
                    @Override
                    public void onResponse(Call<RspModel<MessageCard>> call, Response<RspModel<MessageCard>> response) {
                        RspModel<MessageCard> rspModel = response.body();
                        if(rspModel != null && rspModel.success()){
                            MessageCard rspCard = rspModel.getResult();
                            if(rspCard != null){
                                Factory.getMessageCenter().dispatch(rspCard);
                            }
                        }else{
                            // 解析一下错误的原因
                            Factory.decodeRspCode(rspModel,null);
                            // 通知失败
                            onFailure(call,null);
                        }
                    }

                    @Override
                    public void onFailure(Call<RspModel<MessageCard>> call, Throwable t) {
                        // 通知失败的情况下
                        card.setStatus(Message.STATUS_FAILED);
                        // 通知数据库刷新
                        Factory.getMessageCenter().dispatch(card);
                    }
                });
            }
        });
    }

    // 上传图片
    private static String upLoadPicture(String path) {
        File file = null;
        try {
            // 通过Glide的缓存区间解决了外部权限的问题
            file = Glide.with(Factory.app())
                    .load(path)
                    .downloadOnly(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(file != null){
            // 进行压缩
            String cacheDir = Application.getCacheDirFile().getAbsolutePath();
            String tempFile = String.format("%s/image/Cache_%s.png",cacheDir, SystemClock.uptimeMillis());
            try {
                // 压缩工具类
                if(PicturesCompressor.compressImage(file.getAbsolutePath(),tempFile, Common.Constant.MAX_UPLOAD_IMAGE_LEGGTH)){
                    // 上传文件
                    String ossPath = UploadHelper.uploadImage(tempFile);
                    // 清理缓存
                    StreamUtil.delete(tempFile);
                    return ossPath;
                }
            }catch (Exception e){
                e.printStackTrace();
            }



        }
        return null;
    }

    // 上传语音
    private static String upLoadAudio(String content) {
        File file = new File(content);
        if(!file.exists() || file.length() == 0){
            return null;
        }
        // 上传并返回
        return UploadHelper.uploadRecordAudio(content);
    }

    /**
     * 查询一条消息，这条消息是这个群里最后收到的一条消息
     * @param groupId 群id
     * @return Message
     */
    public static Message findLastWithGroup(String groupId) {
        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.group_id.eq(groupId))
                .orderBy(Message_Table.createAt,false)
                .querySingle();
    }

    /**
     * 和一个人的最后一条聊天消息
     * @param userId 用户id
     * @return Message
     */
    public static Message findLastWithUser(String userId) {
        return SQLite.select()
                .from(Message.class)
                .where(OperatorGroup.clause()
                        .and(Message_Table.sender_id.eq(userId))
                        .and(Message_Table.group_id.isNull()))
                .or(Message_Table.receiver_id.eq(userId))
                .orderBy(Message_Table.createAt,false)
                .querySingle();
    }
}
