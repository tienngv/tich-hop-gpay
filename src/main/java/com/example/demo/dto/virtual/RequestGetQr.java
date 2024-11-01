package com.example.demo.dto.virtual;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestGetQr {
    private String merchant_code;
    private String account_number;

    public RequestGetQr(String merchantCode, String accountNumber) {
        this.merchant_code = merchantCode;
        this.account_number = accountNumber;
    }
}
