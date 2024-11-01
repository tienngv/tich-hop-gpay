package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestBillInfo {
    private String merchant_code;
    private String gpay_bill_id;
    private String merchant_order_id;
    private String signature;

    public RequestBillInfo(String merchant_code, String gpay_bill_id, String merchant_order_id) {
        this.merchant_code = merchant_code;
        this.gpay_bill_id = gpay_bill_id;
        this.merchant_order_id = merchant_order_id;
    }
    public RequestBillInfo(){}
}
