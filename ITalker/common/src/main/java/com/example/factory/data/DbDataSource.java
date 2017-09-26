package com.example.factory.data;

import java.util.List;

/**
 * 基础的数据源数据库接口定义
 * Created by Administrator on 2017/8/24.
 */

public interface DbDataSource<Data> extends DataSource {
    /**
     * 有一个基本的数据源的加载方法
     * @param callback 传递一个callback 一般回调到Presenter
     */
    void load(SucceedCallback<List<Data>> callback);
}
