package com.example.demo.dto.virtual;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequest {
    private String merchant_code;
    private Number from_date;
    private int to_date;
    private int page;
    private int per_page;
    private String signature;
}
