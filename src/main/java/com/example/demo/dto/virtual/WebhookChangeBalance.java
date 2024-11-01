package com.example.demo.dto.virtual;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WebhookChangeBalance {
    private String gpay_trans_id;
    private String bank_trace_id;
    private String bank_transaction_id;
    private String account_number;
    private String message;
    private Number amount;
    private String merchant_code;
    private String action;
    private String signature;



}
