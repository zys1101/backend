package com.contact.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.contact.entity.ContactTagRel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 联系人-标签关联 Mapper
 */
@Mapper
public interface ContactTagRelMapper extends BaseMapper<ContactTagRel> {
}
