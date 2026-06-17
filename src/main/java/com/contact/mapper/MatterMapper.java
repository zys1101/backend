package com.contact.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.contact.entity.Matter;
import org.apache.ibatis.annotations.Mapper;

/**
 * 事项Mapper接口
 *
 * @author Contact Manager
 */
@Mapper
public interface MatterMapper extends BaseMapper<Matter> {

}
