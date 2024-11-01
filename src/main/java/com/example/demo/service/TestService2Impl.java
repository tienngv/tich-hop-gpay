package com.example.demo.service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.example.demo.SignatureUtil;
import com.example.demo.dto.*;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.InputMismatchException;

@Service
@Log4j2
public class TestService2Impl {
    @Autowired
    private Gson gson;

    // 2.3.1 Truy vấn thông tin tài khoản ngân hàng/số thẻ
    public ResponseEntity<?> inquiry(RequestInquiry request) {
        final String url = SignatureUtil.urlMpa + "/fund-transfers/inquiry";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RequestInquiry> requestEntity = new HttpEntity<>(request, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        try {
            String input = "merchant_code=" + SignatureUtil.merchantCode;
            input += "&account_number=" + request.getAccount_number();
            input += "&type=" + request.getType();
            input += "&request_id=" + request.getRequest_id();

            System.out.println(input);
            request.setSignature(SignatureUtil.signData(input, SignatureUtil.privateKeyPEM));

            ResponseEntity<ResultInquiry> result = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    ResultInquiry.class
            );
            log.info("request inquiry [{}] response inquiry [{}]", gson.toJson(request), gson.toJson(result.getBody()));
            return result;
        } catch (InputMismatchException ex) {
            ResponseMeta meta = new ResponseMeta("400", "BAD REQUEST", ex.getMessage());
            ResponseResult response = new ResponseResult(ex.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        } catch (HttpClientErrorException.NotFound ex) {
            ResponseMeta meta = new ResponseMeta("404", "NOT FOUND", "Không tìm thấy thông tin thanh toán");
            ResponseResult response = new ResponseResult(ex.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        } catch (HttpClientErrorException.Conflict e) {
            ResponseMeta meta = new ResponseMeta("409", "CONFLICT", "Xảy ra xung đột, đã tồn tại request này");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        } catch (HttpClientErrorException.Forbidden e) {
            ResponseMeta meta = new ResponseMeta("403", "FORBIDDEN", "Lỗi token");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        } catch (HttpClientErrorException.Unauthorized e) {
            ResponseMeta meta = new ResponseMeta("401", "UNAUTHORIZED", "Lỗi xác thực chữ ký");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 2.3.2 Chuyển tiền từ Ví tới Tài khoản ngân hàng/ số thẻ
    public ResponseEntity<?> ftToBank(RequestFt request) {
        final String url = SignatureUtil.urlMpa + "/fund-transfers/ft-to-bank";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RequestFt> requestEntity = new HttpEntity<>(request, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        try {
            String input = "";
            validateFtToBank(request);
            input += "merchant_code=" + SignatureUtil.merchantCode;
            input += "&account_number=" + request.getAccount_number();
            input += "&bank_code=" + request.getBank_code();
            if(request.getOrder_ref()==null || request.getOrder_ref().isEmpty()){
                input += "&order_ref=";
            }else{
                input += "&order_ref=" + request.getOrder_ref();
            }
            input += "&amount=" + request.getAmount();
            input += "&transaction_id=" + request.getTransaction_id();
            input += "&type=" + request.getType();
            System.out.println(input);
            request.setSignature(SignatureUtil.signData(input, SignatureUtil.privateKeyPEM));
            ResponseEntity<ResponseFtToBankRs> result = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    ResponseFtToBankRs.class
            );
            log.info("request ftToBank [{}] response ftToBank [{}]", gson.toJson(request), gson.toJson(result.getBody()));
            return result;
        } catch (InputMismatchException ex) {
            ResponseMeta meta = new ResponseMeta("400", "BAD REQUEST", ex.getMessage());
            ResponseResult response = new ResponseResult(ex.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        } catch (HttpClientErrorException.NotFound ex) {
            ResponseMeta meta = new ResponseMeta("404", "NOT FOUND", "Không tìm thấy thông tin thanh toán");
            ResponseResult response = new ResponseResult(ex.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        } catch (HttpClientErrorException.Conflict e) {
            ResponseMeta meta = new ResponseMeta("409", "CONFLICT", "Xảy ra xung đột, đã tồn tại request này");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        } catch (HttpClientErrorException.Forbidden e) {
            ResponseMeta meta = new ResponseMeta("403", "FORBIDDEN", "Lỗi token");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        } catch (HttpClientErrorException.Unauthorized e) {
            ResponseMeta meta = new ResponseMeta("401", "UNAUTHORIZED", "Lỗi xác thực chữ ký");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    //2.3.3 Truy vấn thông tin chi tiết giao dịch bằng merchant transaction ID
    public ResponseEntity<?> fundTransferId(String type,String param) {
        final String url = SignatureUtil.urlMpa + "/fund-transfers/" +type+"/" +param;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();
        try {
            if(param==null || param.isEmpty()
            ||type == null || type.isEmpty()){
                throw new InputMismatchException();
            }
            RequestGetInforTrans request = new RequestGetInforTrans();
            request.setMerchant_code(SignatureUtil.merchantCode);
            String inputHeader = "merchant_code=" + SignatureUtil.merchantCode + "&id=" +param;
            HttpEntity<RequestGetInforTrans> requestEntity = new HttpEntity<>(request, httpHeaders);
            request.setSignature(SignatureUtil.signData(inputHeader,SignatureUtil.privateKeyPEM));
            System.out.println(inputHeader);
            ResponseEntity<ResponseFundTransfer> result = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    ResponseFundTransfer.class
            );
            log.info("request fundTransfer [{}] response fundTransfer [{}]", gson.toJson(request), gson.toJson(result.getBody()));
            return result;
        } catch (InputMismatchException ex) {
            ResponseMeta meta = new ResponseMeta("400", "BAD REQUEST", ex.getMessage());
            ResponseResult response = new ResponseResult(ex.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        } catch (HttpClientErrorException.NotFound ex) {
            ResponseMeta meta = new ResponseMeta("404", "NOT FOUND", "Không tìm thấy thông tin thanh toán");
            ResponseResult response = new ResponseResult(ex.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        } catch (HttpClientErrorException.Conflict e) {
            ResponseMeta meta = new ResponseMeta("409", "CONFLICT", "Xảy ra xung đột, đã tồn tại request này");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        } catch (HttpClientErrorException.Forbidden e) {
            ResponseMeta meta = new ResponseMeta("403", "FORBIDDEN", "Lỗi token");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        } catch (HttpClientErrorException.Unauthorized e) {
            ResponseMeta meta = new ResponseMeta("401", "UNAUTHORIZED", "Lỗi xác thực chữ ký");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    //2.3.4 Truy vấn số dư tài khoản
    public ResponseEntity<?> getInfoAccount() {
        final String url = SignatureUtil.urlMpa + "/merchants/get-merchant-account-information";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();
        try {
            RequestGetInforTrans request = new RequestGetInforTrans();
            request.setMerchant_code(SignatureUtil.merchantCode);
            String inputBody = "merchant_code=" + SignatureUtil.merchantCode;
            HttpEntity<RequestGetInforTrans> requestEntity = new HttpEntity<>(request, httpHeaders);
            request.setSignature(SignatureUtil.signData(inputBody,SignatureUtil.privateKeyPEM));
            ResponseEntity<ResponseGetInfoAccount> result = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    ResponseGetInfoAccount.class
            );
            log.info("request get-merchant-account-information [{}] response get-merchant-account-information [{}]", gson.toJson(request), gson.toJson(result.getBody()));
            return result;
        } catch (InputMismatchException ex) {
            ResponseMeta meta = new ResponseMeta("400", "BAD REQUEST", ex.getMessage());
            ResponseResult response = new ResponseResult(ex.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        } catch (HttpClientErrorException.NotFound ex) {
            ResponseMeta meta = new ResponseMeta("404", "NOT FOUND", "Không tìm thấy thông tin thanh toán");
            ResponseResult response = new ResponseResult(ex.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        } catch (HttpClientErrorException.Conflict e) {
            ResponseMeta meta = new ResponseMeta("409", "CONFLICT", "Xảy ra xung đột, đã tồn tại request này");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        } catch (HttpClientErrorException.Forbidden e) {
            ResponseMeta meta = new ResponseMeta("403", "FORBIDDEN", "Lỗi token");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        } catch (HttpClientErrorException.Unauthorized e) {
            ResponseMeta meta = new ResponseMeta("401", "UNAUTHORIZED", "Lỗi xác thực chữ ký");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // web client
    //2.3.4 Truy vấn số dư tài khoản V2
    public ResponseEntity<?> getInfoAccountV2() {
        final String url = SignatureUtil.urlMpa + "/merchants/get-merchant-account-information";
        WebClient webClient = WebClient.builder().baseUrl(SignatureUtil.urlMpa).build();

        try {
            RequestGetInforTrans request = new RequestGetInforTrans();
            request.setMerchant_code(SignatureUtil.merchantCode);
            String inputBody = "merchant_code=" + SignatureUtil.merchantCode;
            request.setSignature(SignatureUtil.signData(inputBody, SignatureUtil.privateKeyPEM));

            Mono<ResponseGetInfoAccount> resultMono = webClient.post()
                    .uri("/merchants/get-merchant-account-information")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ResponseGetInfoAccount.class);

            ResponseGetInfoAccount result = resultMono.block(); // đồng bộ, không đồng bộ sẽ tăng hiệu suất hơn
            log.info("request get-merchant-account-information2 [{}] response get-merchant-account-information2 [{}]",
                    gson.toJson(request), gson.toJson(result));
            return ResponseEntity.ok(result);
        } catch (InputMismatchException ex) {
            ResponseMeta meta = new ResponseMeta("400", "BAD REQUEST", ex.getMessage());
            ResponseResult response = new ResponseResult(ex.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        } catch (HttpClientErrorException.NotFound ex) {
            ResponseMeta meta = new ResponseMeta("404", "NOT FOUND", "Không tìm thấy thông tin thanh toán");
            ResponseResult response = new ResponseResult(ex.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        } catch (HttpClientErrorException.Conflict e) {
            ResponseMeta meta = new ResponseMeta("409", "CONFLICT", "Xảy ra xung đột, đã tồn tại request này");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        } catch (HttpClientErrorException.Forbidden e) {
            ResponseMeta meta = new ResponseMeta("403", "FORBIDDEN", "Lỗi token");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        } catch (HttpClientErrorException.Unauthorized e) {
            ResponseMeta meta = new ResponseMeta("401", "UNAUTHORIZED", "Lỗi xác thực chữ ký");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    //2.3.5 Chuyển tiền từ Ví tới Tài khoản nạp tiền merchant
    public ResponseEntity<?> ftToCashMc(RequestFt request) {
        final String url = SignatureUtil.urlMpa + "/fund-transfers/ft-to-cashmc";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RequestFt> requestEntity = new HttpEntity<>(request, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        try {
            String input = "";
            validateFtToBank(request);
            input += "merchant_code=" + SignatureUtil.merchantCode;
            input += "&account_number=" + request.getAccount_number();
            input += "&bank_code=" + request.getBank_code();
            input += "&amount=" + request.getAmount();
            input += "&transaction_id=" + request.getTransaction_id();
            input += "&type=" + request.getType();
            System.out.println(input);
            request.setSignature(SignatureUtil.signData(input, SignatureUtil.privateKeyPEM));
            ResponseEntity<ResponseFtToBankRs> result = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    ResponseFtToBankRs.class
            );
            log.info("request ftToCashMc [{}] response ftToCashMc [{}]", gson.toJson(request), gson.toJson(result.getBody()));
            return result;
        } catch (InputMismatchException ex) {
            ResponseMeta meta = new ResponseMeta("400", "BAD REQUEST", ex.getMessage());
            ResponseResult response = new ResponseResult(ex.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        } catch (HttpClientErrorException.NotFound ex) {
            ResponseMeta meta = new ResponseMeta("404", "NOT FOUND", "Không tìm thấy thông tin thanh toán");
            ResponseResult response = new ResponseResult(ex.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        } catch (HttpClientErrorException.Conflict e) {
            ResponseMeta meta = new ResponseMeta("409", "CONFLICT", "Xảy ra xung đột, đã tồn tại request này");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        } catch (HttpClientErrorException.Forbidden e) {
            ResponseMeta meta = new ResponseMeta("403", "FORBIDDEN", "Lỗi token");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        } catch (HttpClientErrorException.Unauthorized e) {
            ResponseMeta meta = new ResponseMeta("401", "UNAUTHORIZED", "Lỗi xác thực chữ ký");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta, response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }




    private void validateFtToBank(RequestFt resource) {
        if (resource.getMerchant_code() == null || resource.getMerchant_code().isEmpty()) {
            throw new InputMismatchException("merchant_code can't empty!");
        }
        if (resource.getAccount_number() == null || resource.getAccount_number().isEmpty()) {
            throw new InputMismatchException("account_number can't empty!");
        }
        if (resource.getTransaction_id() == null || resource.getTransaction_id().isEmpty()) {
            throw new InputMismatchException("transaction_id can't empty!");
        }
    }
}
