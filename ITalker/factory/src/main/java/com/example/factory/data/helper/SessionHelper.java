package com.example.factory.data.helper;

import com.example.factory.model.db.Session;
import com.example.factory.model.db.Session_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * 会话的辅助工具类
 * Created by Administrator on 2017/8/24.
 */

public class SessionHelper {
    public static Session findFromLocal(String id){
        // 从本地查询session
        return SQLite.select()
                .from(Session.class)
                .where(Session_Table.id.eq(id))
                .querySingle();
    }
}
