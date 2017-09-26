package com.example.factory.model;


import java.util.Date;

/**
 * 基础的用户接口
 * Created by Administrator on 2017/8/20.
 */

public interface Author {
    String getId();

    void setId(String id);

    String getName();

    void setName(String name);

    String getPortrait();

   void setPortrait(String portrait);


}
