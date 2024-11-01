package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestGetToken {
    private String merchant_code;
    private String password;

    public RequestGetToken(String merchant_code, String password){
        this.merchant_code = merchant_code;
        this.password = password;
    }

    public RequestGetToken(){}
}
