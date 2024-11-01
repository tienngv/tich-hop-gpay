package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseMeta {
    private String code;
    private String msg;
    private String internal_msg;

    public ResponseMeta(String code, String msg, String internalMsg) {
        this.code = code;
        this.msg = msg;
        this.internal_msg = internalMsg;
    }
}
