package com.example.factory.model.db.view;

import com.example.factory.model.db.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;

/**
 * 群成员对应的用户简单的信息表
 * Created by Administrator on 2017/9/12.
 */
@QueryModel(database = AppDatabase.class)
public class MemberUserModel {
    @Column
    public String id; // user-id member-id
    @Column
    public String name; // user-name
    @Column
    public String alias; // member-alias
    @Column
    public String portrait; // user-portrait
}
