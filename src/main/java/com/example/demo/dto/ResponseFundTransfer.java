package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseFundTransfer {
    private ResponseMeta meta;
    private ResponseFt response;
}
