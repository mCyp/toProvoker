package com.example.factory.data.group;

import com.example.factory.model.api.card.GroupCard;
import com.example.factory.model.api.card.GroupMemberCard;

/**
 * 群中心的接口
 * Created by Administrator on 2017/8/23.
 */

public interface GroupCenter {
    // 处理用户发过来的的卡片 并更新到数据库
    void dispatch(GroupCard... cards);

    // 群成员的处理
    void dispatch(GroupMemberCard... cards);
}
