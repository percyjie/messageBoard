package com.percy.messageboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.percy.messageboard.dto.MessageView;
import com.percy.messageboard.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    IPage<MessageView> selectMessagePage(Page<MessageView> page, @Param("userId") Long userId);
}
