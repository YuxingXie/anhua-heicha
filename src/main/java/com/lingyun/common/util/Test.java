package com.lingyun.common.util;

import com.lingyun.entity.User;

import java.util.List;

/**
 * Created by Administrator on 2015/10/23.
 */
class Person{
    private List<User> children;
    private int age;
    private User mate;
    private User[] parents;

    public List<User> getChildren() {
        return children;
    }

    public void setChildren(List<User> children) {
        this.children = children;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public User getMate() {
        return mate;
    }

    public void setMate(User mate) {
        this.mate = mate;
    }

    public User[] getParents() {
        return parents;
    }

    public void setParents(User[] parents) {
        this.parents = parents;
    }
}
