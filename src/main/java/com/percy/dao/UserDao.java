package com.percy.dao;

import com.percy.model.User;

import java.util.List;

/**
 * Created by percy
 * Description:
 * 2016/10/5.
 */
public interface UserDao {


    User findById(int id);

    List<User> findByName(String name);

    List<User> findByEmail(String email);

    void saveUser(User user);

    void updateUser(User user);

    void deleteUser(User user);

    Long findUserCount();

    List<User> findAllUser();

    List<User> findUserByPage(int pageNo, int pageSize);

}
