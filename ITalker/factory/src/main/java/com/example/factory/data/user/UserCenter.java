package com.example.factory.data.user;

import com.example.factory.model.api.card.UserCard;

/**
 * 用户中心的基本定义
 * Created by Administrator on 2017/8/22.
 */

public interface UserCenter {
    // 处理用户发过来的的卡片 并更新到数据库
    void dispatch(UserCard... cards);
}
