package com.percy.messageboard.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.percy.messageboard.dto.MessageView;

public interface MessageService {

    Page<MessageView> page(long current, long size, Long userId);

    void create(Long userId, String content, String ip, String province);

    void deleteOwn(Long messageId, Long userId);

    void deleteAsAdmin(Long messageId, Long expectedUserId);

    void deleteAnyAsAdmin(Long messageId);

    long count();

    long countByUser(Long userId);
}
