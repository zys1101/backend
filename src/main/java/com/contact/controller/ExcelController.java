package com.contact.controller;

import com.contact.common.result.Result;
import com.contact.service.ExcelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Excel导入导出控制器
 */
@Tag(name = "Excel导入导出", description = "联系人Excel批量导入导出接口")
@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;

    /**
     * 导出联系人
     */
    @Operation(summary = "导出联系人", description = "导出当前用户的所有联系人到Excel文件")
    @GetMapping("/export")
    public void exportContacts(HttpServletResponse response) throws IOException {
        excelService.exportContacts(response);
    }

    /**
     * 导入联系人
     */
    @Operation(summary = "导入联系人", description = "从Excel文件批量导入联系人")
    @PostMapping("/import")
    public Result<Map<String, Object>> importContacts(
            @RequestParam("file") MultipartFile file) throws IOException {
        Map<String, Object> result = excelService.importContacts(file);
        return Result.success("导入完成", result);
    }
}
