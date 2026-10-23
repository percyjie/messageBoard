package com.percy.messageboard.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.percy.messageboard.dto.MessageView;

public interface MessageService {

    Page<MessageView> page(long current, long size, Long userId);

    void create(Long userId, String content, String ip);

    void deleteOwn(Long messageId, Long userId);

    void deleteAsAdmin(Long messageId, Long expectedUserId);

    long count();

    long countByUser(Long userId);
}
