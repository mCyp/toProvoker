package com.example.administrator.italker.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.factory.Factory;
import com.example.factory.data.helper.AccountHelper;
import com.example.factory.persistance.Account;
import com.igexin.sdk.PushConsts;

/**
 * Created by Administrator on 2017/8/15.
 */

public class MessagerReceiver extends BroadcastReceiver {

    private static final String TAG = MessagerReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        // 判断intent 是否为空
        if(intent == null)
            return;

        // 获取传来的数据
        Bundle bundle = intent.getExtras();
        // 判断传来的意图
        switch (bundle.getInt(PushConsts.CMD_ACTION)){
            case PushConsts.GET_CLIENTID:{
                // 获取设备Id
                Log.i(TAG,"GET_CLIENTID:"+bundle.toString());
                onClientInit(bundle.getString("clientid"));
                break;
            }
            case PushConsts.GET_MSG_DATA:{
                // 常规信息送达
                byte[] payload =bundle.getByteArray("payload");
                if(payload != null){
                    String mesaage = new String(payload);
                    Log.i(TAG,"GET_MSG_DATA:"+mesaage);
                    onMessageArrived(mesaage);
                }
                break;
            }
            default:
                Log.i(TAG,"OTHER:"+bundle.toString());
                break;
        }

    }

    /**
     * 初始化设备Id的时候
     * @param cid 设备id
     */
    private void onClientInit(String cid){
        Account.setPushId(cid);
        if(Account.isLogin()){
            // 账户登录的状态下 进行一次pushId的绑定
            // 没有登录的情况下是不可以绑定pushId的
            AccountHelper.bindPush(null);
        }
    }

    /**
     * 信息到达的时候
     */
    private void onMessageArrived(String message){
        // 交给我我们的Factory处理
        Factory.dispatchPush(message);
    }



}
