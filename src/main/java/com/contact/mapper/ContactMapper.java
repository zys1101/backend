package com.contact.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.contact.entity.Contact;
import org.apache.ibatis.annotations.Mapper;

/**
 * 联系人Mapper接口
 *
 * @author Contact Manager
 */
@Mapper
public interface ContactMapper extends BaseMapper<Contact> {

}
