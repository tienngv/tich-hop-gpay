package com.example.demo.dto.virtual;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionItem {
    private String gpay_trans_id;
    private String bank_transaction_id;
    private String account_number;
    private String account_name;
    private String status;
    private String created_date;
    private String bank_code;
    private int map_id;
    private String account_type;

}
