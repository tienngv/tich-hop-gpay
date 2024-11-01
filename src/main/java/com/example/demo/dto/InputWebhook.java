package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InputWebhook {
    private String gpay_bill_id;
    private String gpay_trans_id;
    private String merchant_order_id;
    private String status;
    private String embed_data;
    private String user_payment_method;
    private String signature;
}
