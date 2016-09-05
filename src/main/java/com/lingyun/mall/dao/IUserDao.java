package com.lingyun.mall.dao;

import com.lingyun.entity.User;
import com.mongodb.WriteResult;

import java.util.List;

/**
 * Created by Administrator on 2015/5/23.
 */
public interface IUserDao {
    List<User> getAllObjects();

    void saveObject(User user);

    User getObject(String id);

    WriteResult updateObject(String id, String name);

    void deleteObject(String id);

    void createCollection();

    void dropCollection();

    User findByNameAndPwd(String loginName, String loginPwd);

    User findByEmail(String email);
}
