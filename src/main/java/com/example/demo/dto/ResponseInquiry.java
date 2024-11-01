package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseInquiry {
    private String full_name;
    private String order_id;
    private String status;
    private String signature;
}
