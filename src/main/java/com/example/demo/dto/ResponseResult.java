package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class ResponseResult {
    private String bill_id;
    private String request_id;
    private String bill_url;
    private LocalDateTime expired_time;
    private String signature;
    private String result_text;

    public ResponseResult(String billId, String requestId, String billUrl, String signature, LocalDateTime expiredTime) {
        this.bill_id = billId;
        this.request_id = requestId;
        this.bill_url = billUrl;
        this.signature = signature;
        this.expired_time = expiredTime;
    }

    public ResponseResult(String resultText) {
        this.result_text = resultText;
    }

    public ResponseResult() {
    }
}
