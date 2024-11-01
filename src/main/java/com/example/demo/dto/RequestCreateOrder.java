package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestCreateOrder {
    String merchant_code;
    String request_id;
    int amount;
    String title;
    String description;
    String customer_id;
    String customer_name;
    String phone;
    String email;
    String address;
    String callback_url;
    String webhook_url;
    String payment_method;
    String embed_data;
    String payment_type;
    String signature;


}
