package com.percy.service.imp;

import com.percy.dao.UserDao;
import com.percy.service.UserService;
import com.percy.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Created by percy
 * Description:
 * 2016/9/27.
 */
@Transactional
@Service("UserService")
public class UserServiceImp implements UserService {

    @Autowired
    private UserDao userDao;

    public User findById(int id){
        return userDao.findById(id);
    }

    public User findByName(String name){

        List<User> users = userDao.findByName(name);
        if(users.isEmpty()){
            return null;
        }
        User user;
            user =  users.get(0);
        return  user;
    }

    public User findByEmail(String email){

        List<User> users = userDao.findByEmail(email);
        if(users.isEmpty()){
            return null;
        }
        User user;
        user =  users.get(0);
        return  user;
    }

    public void saveUser(User user){
        userDao.saveUser(user);
    }

    public void updateUser( User user){
        userDao.updateUser(user);
    }

    public void deleteUser(User user){
        userDao.deleteUser(user);
    }

    public Long findUserCount(){
        return userDao.findUserCount();
    }

    public List<User> findAllUser(){
        return userDao.findAllUser();
    }

    public List<User> findUserByPage(int pageNo, int pageSize){
        return userDao.findUserByPage(pageNo, pageSize);
    }

    public boolean isUserEmailExist(String email){
        List<User> users = findAllUser();
        for(User user : users){
            if(user.getEmail().equals(email)){
                return true;
            }
        }
        return false;
    }

    public boolean isUserEmailExistExceptSelf(String sqlEmail, String localEmail){
        List<User> users = findAllUser();
        for(User user : users){
            if(!user.getEmail().equals(localEmail)){
                if(user.getEmail().equals(sqlEmail)){
                    return true;
                }
            }
        }
        return false;
    }

}
