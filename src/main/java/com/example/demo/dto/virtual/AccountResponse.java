package com.example.demo.dto.virtual;

import com.example.demo.dto.ResponseMeta;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountResponse {
    private String account_number;
    private String account_name;
    private String status;
    private String start_at;
    private String expire_at;
    private String balance;
    private String account_type;
    private int max_amount;
    private int min_amount;
    private int equal_amount;
    private String qr_code;
    private String qr_code_image;
    private String signature;

}
