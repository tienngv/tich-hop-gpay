package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponeResultGetBill {
    private String merchant_order_id;
    private String gpay_trans_id;
    private String gpay_bill_id;
    private String status;
    private String embed_data;
    private String user_payment_method;
    private String signature;

}
