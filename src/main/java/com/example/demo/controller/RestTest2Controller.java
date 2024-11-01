package com.example.demo.controller;

import com.example.demo.dto.RequestFt;
import com.example.demo.dto.RequestInquiry;
import com.example.demo.service.TestService2Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/g-pay/fund-transfers")
public class RestTest2Controller {
    @Autowired
    TestService2Impl service;

    @GetMapping("/test")
    public int testInt(@RequestParam("number") int number){
        return number;
    }

    @PostMapping("/inquiry")
    public ResponseEntity<?> createBill(@RequestBody RequestInquiry request) {
        return service.inquiry(request);
    }
    @PostMapping("/ft-to-bank")
    public ResponseEntity<?> ftToBank(@RequestBody RequestFt request) {
        return service.ftToBank(request);
    }
    @PostMapping("/ft-to-cashmc")
    public ResponseEntity<?> ftToCashMc(@RequestBody RequestFt request) {
        return service.ftToCashMc(request);
    }

    @PostMapping("/{w2b}/{transId}")
    public ResponseEntity<?> ftToBank(@PathVariable String type,@PathVariable String transId) {
        return service.fundTransferId(type,transId);
    }

    @PostMapping("/get-merchant-account-information")
    public ResponseEntity<?> getInfoAccountV2() {
        return service.getInfoAccount();
    }
}
