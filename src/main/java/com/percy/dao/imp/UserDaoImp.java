package com.percy.dao.imp;

import com.percy.dao.UserDao;
import com.percy.model.User;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by percy
 * Description:
 * 2016/10/5.
 */
@Repository("UserDao")
public class UserDaoImp extends BaseDaoImp<User>  implements UserDao {

    public User findById(int id){
        return get(User.class, id);

    }

    public List<User> findByName(String name){
        String hql = "from User where name =?";
        return find(hql, name);

    }

    public List<User> findByEmail(String email){
        String hql = "from User where email =?";
        return find(hql , email);
    }

    public void saveUser(User user){
        getHibernateTemplate().save(user);
    }

    public void updateUser(User user){
        getHibernateTemplate().update(user);
    }

    public void deleteUser(User user){
        getHibernateTemplate().delete(user);
    }

    public Long findUserCount(){
        String hql = "select count(*) from User as user";
        return (Long)getHibernateTemplate().find(hql).listIterator().next();
    }

    public List<User> findAllUser(){
        DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
        return (List<User>) getHibernateTemplate().findByCriteria(criteria);
    }

    public List<User> findUserByPage(int pageNo, int pageSize){
        String hql = "from User";
        return findByPage(hql, pageNo, pageSize);
    }
}
