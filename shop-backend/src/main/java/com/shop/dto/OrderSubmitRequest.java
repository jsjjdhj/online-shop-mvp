package com.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderSubmitRequest {

    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 50, message = "收货人姓名不超过50个字符")
    private String recipientName;

    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入有效的11位手机号码")
    private String recipientPhone;

    @NotBlank(message = "详细地址不能为空")
    @Size(max = 255, message = "详细地址不超过255个字符")
    private String recipientAddress;
}
