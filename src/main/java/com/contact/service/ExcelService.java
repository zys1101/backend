package com.contact.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.contact.dto.ContactCreateDTO;
import com.contact.vo.ContactVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Excel导入导出服务接口
 */
public interface ExcelService {

    /**
     * 导出联系人到Excel
     */
    void exportContacts(HttpServletResponse response) throws IOException;

    /**
     * 导入联系人Excel
     */
    Map<String, Object> importContacts(MultipartFile file) throws IOException;
}
