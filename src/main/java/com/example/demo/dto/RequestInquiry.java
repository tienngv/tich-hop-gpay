package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestInquiry {
    private String request_id;
    private String merchant_code;
    private String account_number;
    private String type;
    private String bank_code;
    private String signature;

}
