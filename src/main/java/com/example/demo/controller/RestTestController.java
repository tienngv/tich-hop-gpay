package com.example.demo.controller;

import com.example.demo.dto.InputWebhook;
import com.example.demo.dto.RequestBillInfo;
import com.example.demo.dto.RequestCreateOrder;

import com.example.demo.service.TestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/g-pay")
public class RestTestController {
    @Autowired
    TestServiceImpl service;

    @GetMapping("/test")
    public int testInt(@RequestParam("number") int number){
        return number;
    }

    @PostMapping("/init-bill")
    public ResponseEntity<?> createBill(@RequestBody RequestCreateOrder request) {
        return service.createBill(request);
    }
    @PostMapping("/bill-info")
    public ResponseEntity<?> getBillInfo(@RequestBody RequestBillInfo request) {
        return service.getBillInfo(request);
    }

    @PostMapping("call-back")
    public ResponseEntity<?> getBillInfo(@RequestBody InputWebhook body) {
        System.out.println("Webhook :"+ body.toString());
        return service.handleResult(body);
    }


}
