package com.percy.messageboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.percy.messageboard.dto.MessageView;
import com.percy.messageboard.entity.Message;
import com.percy.messageboard.mapper.MessageMapper;
import com.percy.messageboard.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;

    @Override
    public Page<MessageView> page(long current, long size, Long userId) {
        Page<MessageView> page = queryPage(Math.max(current, 1), size, userId);
        if (page.getPages() > 0 && page.getCurrent() > page.getPages()) {
            page = queryPage(page.getPages(), size, userId);
        }
        return page;
    }

    @Override
    @Transactional
    public void create(Long userId, String content, String ip) {
        String text = content == null ? "" : content.trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException("留言内容不能为空");
        }
        Message message = new Message();
        message.setUserId(userId);
        message.setContent(text);
        message.setIp(ip);
        message.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(message);
    }

    @Override
    @Transactional
    public void deleteOwn(Long messageId, Long userId) {
        Message message = requireMessage(messageId);
        if (!Objects.equals(message.getUserId(), userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "留言不存在");
        }
        messageMapper.deleteById(messageId);
    }

    @Override
    @Transactional
    public void deleteAsAdmin(Long messageId, Long expectedUserId) {
        Message message = requireMessage(messageId);
        if (!Objects.equals(message.getUserId(), expectedUserId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "留言不存在");
        }
        messageMapper.deleteById(messageId);
    }

    @Override
    public long count() {
        return messageMapper.selectCount(null);
    }

    @Override
    public long countByUser(Long userId) {
        return messageMapper.selectCount(new LambdaQueryWrapper<Message>().eq(Message::getUserId, userId));
    }

    private Page<MessageView> queryPage(long current, long size, Long userId) {
        Page<MessageView> page = new Page<>(current, size);
        messageMapper.selectMessagePage(page, userId);
        return page;
    }

    private Message requireMessage(Long id) {
        Message message = messageMapper.selectById(id);
        if (message == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "留言不存在");
        }
        return message;
    }
}
