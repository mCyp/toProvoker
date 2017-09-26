package com.example.factory.model.api.group;

import java.util.HashSet;
import java.util.Set;

/**
 * 添加群成员的model
 * Created by Administrator on 2017/9/11.
 */

public class GroupMemberAddModel {
    private Set<String> users = new HashSet<>();

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }
}
