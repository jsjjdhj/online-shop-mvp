package com.shop.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称不超过100个字符")
    private String name;

    @Size(max = 2000, message = "商品描述不超过2000个字符")
    private String description;

    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    @DecimalMax(value = "999999.99", message = "商品价格超出范围")
    private BigDecimal price;

    @NotNull(message = "库存数量不能为空")
    @Min(value = 0, message = "库存数量不能为负数")
    @Max(value = 99999, message = "库存数量超出范围")
    private Integer stock;

    private String imageUrl;
}
