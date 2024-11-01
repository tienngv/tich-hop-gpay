package com.example.demo.dto.virtual;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TransactionResponse {
    private List<TransactionItem> items;
    private PaginationResponse pagination;
    private String signature;
}
