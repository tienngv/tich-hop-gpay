package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseCreateOrder {
    private ResponseMeta meta;
    private ResponseResult response;

    public ResponseCreateOrder(ResponseMeta meta, ResponseResult response) {
        this.meta = meta;
        this.response = response;
    }
}
