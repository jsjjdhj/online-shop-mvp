package com.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderSubmitRequest {

    @NotBlank(message = "收货人姓名不能为空")
    @Size(min = 2, max = 20, message = "收货人姓名长度为2-20个字符")
    @Pattern(regexp = "^[\u4e00-\u9fa5a-zA-Z]+$", message = "收货人姓名仅限中文汉字或英文字母")
    private String recipientName;

    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入有效的11位手机号码")
    private String recipientPhone;

    @NotBlank(message = "详细地址不能为空")
    @Size(min = 10, max = 100, message = "详细地址长度为10-100个字符")
    private String recipientAddress;
}
