package com.contact.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.contact.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 *
 * @author Contact Manager
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

}
