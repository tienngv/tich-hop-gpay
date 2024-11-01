package com.example.demo.controller;

import com.example.demo.dto.RequestBillInfo;
import com.example.demo.dto.virtual.AccountRequest;
import com.example.demo.dto.virtual.TransactionRequest;
import com.example.demo.service.VirtualAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/g-pay/virtual-account")
public class RestVirtualAccountController {
    @Autowired
    VirtualAccountService service;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody AccountRequest request) {
        return service.createVirtualAccount(request);
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody AccountRequest request) {
        return service.updateVirtualAccount(request);
    }

    @PostMapping("/close")
    public ResponseEntity<?> close(@RequestBody AccountRequest request) {
        return service.closeVirtualAccount(request);
    }

    @PostMapping("/detail")
    public ResponseEntity<?> detail(@RequestBody AccountRequest request) {
        return service.detailVirtualAccount(request);
    }

    @PostMapping("/re-open")
    public ResponseEntity<?> reOpen(@RequestBody AccountRequest request) {
        return service.detailVirtualAccount(request);
    }
    @PostMapping("/list-transaction")
    public ResponseEntity<?> getTransaction(@RequestBody TransactionRequest request) {
        return service.getTransaction(request);
    }
    @GetMapping("/viet-qr/{merchant_code}/{account_number}")
    public ResponseEntity<?> getTransaction(@PathVariable("merchant_code") String mcCode,
                                            @PathVariable("account_number") String accountNumber) {
        return service.getVietQrImg(mcCode,accountNumber);
    }

}
