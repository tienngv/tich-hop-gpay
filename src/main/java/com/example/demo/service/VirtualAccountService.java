package com.example.demo.service;

import com.example.demo.SignatureUtil;
import com.example.demo.dto.*;
import com.example.demo.dto.virtual.*;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.InputMismatchException;
import java.util.Objects;

@Log4j2
@Service
public class VirtualAccountService {
    @Autowired
    private Gson gson;

    //-**- GET TOKEN -**-
    public ResponseEntity<?> getToken() {
        final String url = SignatureUtil.urlVirtualAccount +"authentication/token/create";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        RequestGetToken request = new RequestGetToken(SignatureUtil.merchantCode, SignatureUtil.password);
        HttpEntity<RequestGetToken> requestEntity = new HttpEntity<>(request, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    ResponseResultGetToken.class
            );
        }catch (HttpClientErrorException.NotFound ex) {
            ResponseMeta meta = new ResponseMeta("404","NOT FOUND","Không tìm thấy merchant");
            ResponseResult response = new ResponseResult(ex.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta,response);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        } catch (HttpClientErrorException.Unauthorized e) {
            ResponseMeta meta = new ResponseMeta("401","UNAUTHORIZED","Lỗi xác thực, sai mật khẩu");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta,response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    //3.3.1 Tạo Tài khoản ảo
    public ResponseEntity<?> createVirtualAccount(AccountRequest request) {
        final String url = SignatureUtil.urlVirtualAccount + "virtual-account/create";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);


        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseResultGetToken responseResultGetToken = (ResponseResultGetToken) getToken().getBody();
            String token = responseResultGetToken.getResponse().getToken();
            httpHeaders.add("Authorization" ,"Bearer "+ token);

            HttpEntity<AccountRequest> requestEntity = new HttpEntity<>(request, httpHeaders);

            String input = "merchant_code=" + SignatureUtil.merchantCode;
            input += "&account_name=" + request.getAccount_name();
            input += "&map_id=" + request.getMap_id();
            input += "&map_type=" + request.getMap_type();
            input += "&account_type=" + request.getAccount_type();
            input += "&bank_code=" +request.getBank_code();

            System.out.println(input);
            request.setMerchant_code(SignatureUtil.merchantCode);
            request.setSignature(SignatureUtil.signData(input, SignatureUtil.privateKeyPEM));

            ResponseEntity<ResultAccountResponse> result = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    ResultAccountResponse.class
            );
            log.info("request createVirtualAccount [{}] response createVirtualAccount [{}]", gson.toJson(request), gson.toJson(result.getBody()));
            return new ResponseEntity<>(result.getBody(), HttpStatus.OK);
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

    //3.3.2 Cập nhật thông tin tài khoản ả
    public ResponseEntity<?> updateVirtualAccount(AccountRequest request) {
        final String url = SignatureUtil.urlVirtualAccount + "virtual-account/update";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AccountRequest> requestEntity = new HttpEntity<>(request, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseResultGetToken responseResultGetToken = (ResponseResultGetToken) getToken().getBody();
            String token = responseResultGetToken.getResponse().getToken();
            httpHeaders.add("Authorization" ,"Bearer "+ token);

            String input = "merchant_code=" + SignatureUtil.merchantCode;
            input += "&account_number=" + request.getAccount_number();
            input += "&account_name=" + request.getAccount_name();

            System.out.println(input);
            request.setMerchant_code(SignatureUtil.merchantCode);
            request.setSignature(SignatureUtil.signData(input, SignatureUtil.privateKeyPEM));

            ResponseEntity<ResultAccountResponse> result = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    ResultAccountResponse.class
            );
//            log.info("request updateVirtualAccount [{}] response updateVirtualAccount [{}]", gson.toJson(request), gson.toJson(result.getBody()));
            return new ResponseEntity<>(result.getBody(), HttpStatus.OK);
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

    //3.3.3 Đóng tài khoản ảo
    public ResponseEntity<?> closeVirtualAccount(AccountRequest request) {
        final String url = SignatureUtil.urlVirtualAccount + "virtual-account/update";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AccountRequest> requestEntity = new HttpEntity<>(request, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseResultGetToken responseResultGetToken = (ResponseResultGetToken) getToken().getBody();
            String token = responseResultGetToken.getResponse().getToken();
            httpHeaders.add("Authorization" ,"Bearer "+ token);

            String input = "merchant_code=" + SignatureUtil.merchantCode;
            input += "&account_number=" + request.getAccount_number();
            System.out.println(input);
            request.setMerchant_code(SignatureUtil.merchantCode);
            request.setSignature(SignatureUtil.signData(input, SignatureUtil.privateKeyPEM));
            ResponseEntity<ResultAccountResponse> result = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    ResultAccountResponse.class
            );
            log.info("request updateVirtualAccount [{}] response updateVirtualAccount [{}]", gson.toJson(request), gson.toJson(result.getBody()));
            return new ResponseEntity<>(result.getBody(), HttpStatus.OK);
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

    //3.3.4 Truy vấn thông tin tài khoản ảo
    public ResponseEntity<?> detailVirtualAccount(AccountRequest request) {
        final String url = SignatureUtil.urlVirtualAccount + "virtual-account/update";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);



        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseResultGetToken responseResultGetToken = (ResponseResultGetToken) getToken().getBody();
            String token = responseResultGetToken.getResponse().getToken();
            httpHeaders.add("Authorization" ,"Bearer "+ token);
            HttpEntity<AccountRequest> requestEntity = new HttpEntity<>(request, httpHeaders);

            String input = "merchant_code=" + SignatureUtil.merchantCode;
            input += "&account_number=" + request.getAccount_number();
            System.out.println(input);
            request.setMerchant_code(SignatureUtil.merchantCode);
            request.setSignature(SignatureUtil.signData(input, SignatureUtil.privateKeyPEM));
            ResponseEntity<ResultAccountResponse> result = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    ResultAccountResponse.class
            );
            log.info("request detailVirtualAccount [{}] response detailVirtualAccount [{}]", gson.toJson(request), gson.toJson(result.getBody()));
            return new ResponseEntity<>(result.getBody(), HttpStatus.OK);
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

    //3.3.5 Re-open tài khoản ảo
    public ResponseEntity<?> reOpenVirtualAccount(AccountRequest request) {
        final String url = SignatureUtil.urlVirtualAccount + "virtual-account/update";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);



        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseResultGetToken responseResultGetToken = (ResponseResultGetToken) getToken().getBody();
            String token = responseResultGetToken.getResponse().getToken();
            httpHeaders.add("Authorization" ,"Bearer "+ token);
            HttpEntity<AccountRequest> requestEntity = new HttpEntity<>(request, httpHeaders);

            String input = "merchant_code=" + SignatureUtil.merchantCode;
            input += "&account_number=" + request.getAccount_number();
            System.out.println(input);
            request.setMerchant_code(SignatureUtil.merchantCode);
            request.setSignature(SignatureUtil.signData(input, SignatureUtil.privateKeyPEM));
            ResponseEntity<ResultAccountResponse> result = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    ResultAccountResponse.class
            );
            log.info("request reOpenVirtualAccount [{}] response reOpenVirtualAccount [{}]", gson.toJson(request), gson.toJson(result.getBody()));
            return new ResponseEntity<>(result.getBody(), HttpStatus.OK);
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

    //3.3.6 Get VietQR Image
    public ResponseEntity<?> getVietQrImg(String merchantCode, String accountNumber) {
        final String url = SignatureUtil.urlVirtualAccount + "virtual-account/viet-qr/"+ merchantCode +"/"+ accountNumber;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();
        try {
            RequestGetQr request = new RequestGetQr(SignatureUtil.merchantCode,accountNumber);
            HttpEntity<RequestGetQr> requestEntity = new HttpEntity<>(request, httpHeaders);

            ResponseEntity<?> result = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    Objects.class
            );
            log.info("request getVietQrImg [{}] response getVietQrImg [{}]", gson.toJson(request), gson.toJson(result.getBody()));
            return new ResponseEntity<>(result.getBody(), HttpStatus.OK);
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

    //3.3.7 Truy vấn thông tin lịch sử giao dịch của Merchant
    public ResponseEntity<?> getTransaction(TransactionRequest request) {
        final String url = SignatureUtil.urlVirtualAccount + "virtual-account/list-transaction";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();
        try {

            ResponseResultGetToken responseResultGetToken = (ResponseResultGetToken) getToken().getBody();
            String token = responseResultGetToken.getResponse().getToken();
            httpHeaders.add("Authorization" ,"Bearer "+ token);
            HttpEntity<TransactionRequest> requestEntity = new HttpEntity<>(request, httpHeaders);

            String input = "merchant_code=" + SignatureUtil.merchantCode;
            input += "&from_date=" + request.getFrom_date();
            input += "&to_date=" + request.getTo_date();
            input += "&page=" + request.getPage();
            input += "&per_page=" + request.getPer_page();

            System.out.println(input);
            request.setMerchant_code(SignatureUtil.merchantCode);
            request.setSignature(SignatureUtil.signData(input, SignatureUtil.privateKeyPEM));
            ResponseEntity<ResultTransactionResponse> result = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    ResultTransactionResponse.class
            );
            log.info("request getTransaction [{}] response getTransaction [{}]", gson.toJson(request), gson.toJson(result.getBody()));
            return new ResponseEntity<>(result.getBody(), HttpStatus.OK);
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
}
