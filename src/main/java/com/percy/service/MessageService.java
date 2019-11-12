package com.percy.service;

import com.percy.model.Message;
import com.percy.vo.MessageJsonBean;

import java.util.List;

/**
 * Created by percy
 * Description:
 * 2016/10/7.
 */
public interface MessageService {

    List<Message> findMessagesByUserId(int id);

    Message findMessageById(int id);

    List<MessageJsonBean> findAllMessage();

    void saveMessage(Message message);

    void deleteMessage(Message message);

    void deleteMessageById(int id);

    Long findMessageCount();

    String getDate();

    List<MessageJsonBean> findMessageByPage(int pageNo,int pageSize );
}
