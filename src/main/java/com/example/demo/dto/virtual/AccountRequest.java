package com.example.demo.dto.virtual;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequest {
    private String merchant_code;
    private String account_name;
    private String map_id;
    private String map_type;
    private String account_type;
    private String bank_code;
    private int max_amount;
    private int min_amount;
    private int equal_amount;
    private String customer_address;
    private String signature;
    //update
    private String account_number;
    //delete
    private String close_reason;
}
