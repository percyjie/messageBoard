package com.percy.dao;

import com.percy.model.Message;
import com.percy.vo.MessageJsonBean;

import java.util.List;

/**
 * Created by 李呈云
 * Description:
 * 2016/10/7.
 */
public interface MessageDao {
    List<Message> findMessagesByUserId(int id);

    Message findMessageById(int id);

    List<MessageJsonBean> findAllMessage();

    void saveMessage(Message message);

    void deleteMessage(Message message);

    Long findMessageCount();

    List<MessageJsonBean> findMessageByPage(final int pageNo,final int pageSize );
}
