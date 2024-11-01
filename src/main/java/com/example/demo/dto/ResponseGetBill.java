package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseGetBill {
    private ResponseMeta meta;
    private ResponeResultGetBill response;
}
