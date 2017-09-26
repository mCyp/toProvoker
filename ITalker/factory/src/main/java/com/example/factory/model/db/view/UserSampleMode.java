package com.example.factory.model.db.view;

import com.example.factory.model.Author;
import com.example.factory.model.db.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;

/**
 * 用户的接触信息Model 可以和数据库查询
 * Created by Administrator on 2017/9/8.
 */
@QueryModel(database = AppDatabase.class)
public class UserSampleMode implements Author{
    @Column
    private String id;
    @Column
    private String name;
    @Column
    private String portrait;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPortrait() {
        return portrait;
    }

    @Override
    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
}