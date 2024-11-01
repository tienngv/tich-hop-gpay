package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ResponseToken {
    private String token;
    private LocalDateTime expired_at;
}
