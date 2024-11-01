package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestFt {
    private String account_number;
    private String bank_code;
    private String full_name;
    private Integer amount;
    private String merchant_code;
    private String transaction_id;
    private String type;  //ACCOUNT_NUMBER, CARD, NUMBER
    private String order_ref;
    private String map_id;
    private String message;
    private String signature;
}
