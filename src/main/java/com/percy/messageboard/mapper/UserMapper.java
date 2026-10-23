package com.percy.messageboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.percy.messageboard.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
