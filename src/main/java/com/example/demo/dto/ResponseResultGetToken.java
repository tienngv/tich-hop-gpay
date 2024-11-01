package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseResultGetToken {
    private ResponseMeta meta;
    private ResponseToken response;
}
