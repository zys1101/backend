package com.contact.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 联系人关联标签DTO
 */
@Data
public class TagAssignDTO {
    
    @NotEmpty(message = "标签ID列表不能为空")
    private List<String> tagIds;
}
