package com.example.factory.data.user;

import android.text.TextUtils;

import com.example.factory.data.helper.DbHelper;
import com.example.factory.model.api.card.UserCard;
import com.example.factory.model.db.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/8/23.
 */

public class UserDispatcher implements UserCenter {

    private static UserCenter instance;
    // 单线程池 处理卡片的一个个消息
    private final Executor executor = Executors.newSingleThreadExecutor();

    public static UserCenter instance(){
        if(instance == null){
            synchronized (UserDispatcher.class){
                if(instance == null)
                    instance = new UserDispatcher();
            }
        }
        return  instance;
    }

    @Override
    public void dispatch(UserCard... cards) {
        if(cards==null || cards.length == 0)
            return;
        executor.execute(new UserCardHandler(cards));
    }


    private class UserCardHandler implements Runnable{

        private UserCard[] userCards;

        public UserCardHandler(UserCard[] userCards) {
            this.userCards = userCards;
        }

        @Override
        public void run() {
            List<User> users = new ArrayList<>();

            for (UserCard card : userCards) {
                // 进行过滤操作
                if(card == null || TextUtils.isEmpty(card.getId()))
                    continue;
                // 添加操作
                users.add(card.build());
            }
            // 进行数据库存储 并分发通知 异步的操作
            DbHelper.save(User.class,users.toArray(new User[0]));
        }
    }
}
