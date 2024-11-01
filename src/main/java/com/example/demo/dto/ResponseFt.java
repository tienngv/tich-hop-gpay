package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseFt {
    private String transfer_id;
    private String transfer_status;
    private String order_id;
    private String transfer_created_time;
    private String transfer_status_updated_time;
    private String signature;
}
