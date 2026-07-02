package com.shop.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotBlank(message = "商品名称不能为空")
    @Size(min = 2, max = 50, message = "商品名称长度为2-50个字符")
    private String name;

    @NotBlank(message = "商品描述不能为空")
    @Size(min = 10, max = 500, message = "商品描述长度为10-500个字符")
    private String description;

    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "商品价格必须大于0")
    @DecimalMax(value = "999999.99", message = "商品价格超出范围")
    @Digits(integer = 6, fraction = 2, message = "商品价格最多保留两位小数")
    private BigDecimal price;

    @NotNull(message = "库存数量不能为空")
    @Min(value = 0, message = "库存数量不能为负数")
    @Max(value = 99999, message = "库存数量超出范围")
    private Integer stock;

    private String imageUrl;
}
