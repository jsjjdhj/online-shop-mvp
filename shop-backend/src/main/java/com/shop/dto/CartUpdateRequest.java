package com.shop.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartUpdateRequest {

    @NotNull(message = "数量不能为空")
    @Min(value = 0, message = "数量不能为负数")
    @Max(value = 99, message = "数量不能超过99")
    private Integer quantity;
}
