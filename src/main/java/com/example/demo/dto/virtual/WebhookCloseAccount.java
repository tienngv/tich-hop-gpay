package com.example.demo.dto.virtual;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebhookCloseAccount {
    private String account_number;
    private String merchant_code;
    private String action;
    private String message;
    private String signature;
}
