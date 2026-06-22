package com.contact.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.contact.common.constant.ErrorCode;
import com.contact.common.constant.SystemConstants;
import com.contact.common.exception.BusinessException;
import com.contact.common.utils.IdGenerator;
import com.contact.common.utils.UserContext;
import com.contact.entity.Contact;
import com.contact.mapper.ContactMapper;
import com.contact.service.ExcelService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Excel导入导出服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

    private final ContactMapper contactMapper;

    @Override
    public void exportContacts(HttpServletResponse response) throws IOException {
        String userId = UserContext.getUserId();
        log.info("导出联系人Excel: userId={}", userId);

        // 查询所有正常联系人
        LambdaQueryWrapper<Contact> query = new LambdaQueryWrapper<>();
        query.eq(Contact::getUserId, userId)
             .eq(Contact::getCtDelete, SystemConstants.CONTACT_STATUS_NORMAL)
             .orderByAsc(Contact::getCtName);
        List<Contact> contacts = contactMapper.selectList(query);

        // 创建Excel
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("联系人列表");

            // 创建标题行样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"序号", "姓名", "性别", "手机号", "邮箱", "地址", "邮编", "QQ号", "微信号", "出生日期"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 20 * 256);
            }

            // 填充数据
            for (int i = 0; i < contacts.size(); i++) {
                Contact c = contacts.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(c.getCtName());
                row.createCell(2).setCellValue(c.getCtMf());
                row.createCell(3).setCellValue(c.getCtPhone());
                row.createCell(4).setCellValue(c.getCtEm() != null ? c.getCtEm() : "");
                row.createCell(5).setCellValue(c.getCtAd() != null ? c.getCtAd() : "");
                row.createCell(6).setCellValue(c.getCtYb() != null ? c.getCtYb() : "");
                row.createCell(7).setCellValue(c.getCtQq() != null ? c.getCtQq() : "");
                row.createCell(8).setCellValue(c.getCtWx() != null ? c.getCtWx() : "");
                row.createCell(9).setCellValue(c.getCtBirth() != null ? c.getCtBirth().toString() : "");
            }

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=contacts.xlsx");

            // 输出
            workbook.write(response.getOutputStream());
            response.flushBuffer();
        }

        log.info("导出联系人Excel完成: 共{}条", contacts.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importContacts(MultipartFile file) throws IOException {
        String userId = UserContext.getUserId();
        log.info("导入联系人Excel: userId={}", userId);

        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR, "文件不能为空");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR, "仅支持 .xlsx 或 .xls 格式");
        }

        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR, "Excel文件中没有工作表");
            }

            // 从第2行开始读取数据（第1行是标题）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String name = getCellValue(row.getCell(1));
                    String gender = getCellValue(row.getCell(2));
                    String phone = getCellValue(row.getCell(3));
                    String email = getCellValue(row.getCell(4));
                    String address = getCellValue(row.getCell(5));
                    String zip = getCellValue(row.getCell(6));
                    String qq = getCellValue(row.getCell(7));
                    String wx = getCellValue(row.getCell(8));
                    String birth = getCellValue(row.getCell(9));

                    if (name == null || name.trim().isEmpty()) {
                        errors.add("第" + (i + 1) + "行：姓名不能为空");
                        failCount++;
                        continue;
                    }

                    if (phone == null || phone.trim().isEmpty()) {
                        errors.add("第" + (i + 1) + "行：手机号不能为空");
                        failCount++;
                        continue;
                    }

                    // 检查手机号是否已存在
                    LambdaQueryWrapper<Contact> existQuery = new LambdaQueryWrapper<>();
                    existQuery.eq(Contact::getUserId, userId)
                              .eq(Contact::getCtPhone, phone);
                    if (contactMapper.selectCount(existQuery) > 0) {
                        errors.add("第" + (i + 1) + "行：手机号已存在 - " + name);
                        failCount++;
                        continue;
                    }

                    // 创建联系人
                    Contact contact = new Contact();
                    contact.setCtId(IdGenerator.generateContactId());
                    contact.setUserId(userId);
                    contact.setCtName(name);
                    contact.setCtMf(gender != null && !gender.isEmpty() ? gender : "男");
                    contact.setCtPhone(phone);
                    contact.setCtEm(email);
                    contact.setCtAd(address);
                    contact.setCtYb(zip);
                    contact.setCtQq(qq);
                    contact.setCtWx(wx);
                    if (birth != null && !birth.isEmpty()) {
                        try {
                            contact.setCtBirth(LocalDate.parse(birth));
                        } catch (Exception e) {
                            errors.add("第" + (i + 1) + "行：日期格式错误 - " + birth);
                        }
                    }
                    contact.setCtDelete(SystemConstants.CONTACT_STATUS_NORMAL);
                    contact.setCreatedAt(LocalDateTime.now());
                    contact.setUpdatedAt(LocalDateTime.now());

                    contactMapper.insert(contact);
                    successCount++;

                } catch (Exception e) {
                    errors.add("第" + (i + 1) + "行：导入失败 - " + e.getMessage());
                    failCount++;
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("errors", errors);

        log.info("导入联系人Excel完成: 成功{}条, 失败{}条", successCount, failCount);
        return result;
    }

    /**
     * 获取单元格值
     */
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                // 电话号码等数字，去掉小数点
                double numValue = cell.getNumericCellValue();
                if (numValue == (long) numValue) {
                    return String.valueOf((long) numValue);
                }
                return String.valueOf(numValue);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
