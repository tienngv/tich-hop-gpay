package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultInquiry {
    private ResponseMeta meta;
    private ResponseInquiry response;
}
