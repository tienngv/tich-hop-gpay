package com.example.demo.dto.virtual;

import com.example.demo.dto.ResponseMeta;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultTransactionResponse {
    private ResponseMeta meta;
    private TransactionResponse response;
}
