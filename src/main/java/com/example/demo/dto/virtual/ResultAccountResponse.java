package com.example.demo.dto.virtual;

import com.example.demo.dto.ResponseMeta;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultAccountResponse {
    private ResponseMeta meta;
    private AccountResponse response;
}
