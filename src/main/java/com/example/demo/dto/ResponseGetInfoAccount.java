package com.example.demo.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseGetInfoAccount {
    private Meta meta;
    private Response response;

    @Getter
    @Setter
    public static class Meta {
        @JsonProperty("code")
        private int code;
        @JsonProperty("msg")
        private String msg;
        @JsonProperty("internal_msg")
        private String internalMsg;

    }
    public static class Response {
        @JsonProperty("amount_revenue")
        private long amountRevenue;

        @JsonProperty("amount_cash")
        private long amountCash;

        @JsonProperty("amount_minimum")
        private long amountMinimum;

        @JsonProperty("amount_refund")
        private long amountRefund;

        @JsonProperty("cash_in_information")
        private CashInInformation cashInInformation;

        private String signature;

    }
    public static class CashInInformation {
        @JsonProperty("account_number")
        private String accountNumber;

        @JsonProperty("account_name")
        private String accountName;

        @JsonProperty("bank_code")
        private String bankCode;

        @JsonProperty("bank_name")
        private String bankName;

        @JsonProperty("qr_code")
        private String qrCode;

        @JsonProperty("qr_code_image")
        private String qrCodeImage;

    }
}

