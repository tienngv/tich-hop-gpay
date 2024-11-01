package com.example.demo.dto.virtual;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
public class PaginationResponse {
    private int page;
    private int total_item;
    private int per_page;
}
