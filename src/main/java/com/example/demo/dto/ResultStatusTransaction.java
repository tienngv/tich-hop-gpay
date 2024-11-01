package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultStatusTransaction {
    private String result;
    private String status;
    private String gpay_trans_id;
    private String gpay_bill_id;
    private String merchant_order_id;
}
