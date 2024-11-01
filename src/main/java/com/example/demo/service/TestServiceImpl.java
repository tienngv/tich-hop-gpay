package com.example.demo.service;
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
public class TestServiceImpl {
    @Autowired
    private Gson gson;

    public ResponseEntity<?> createBill(RequestCreateOrder request) {
        final String url = SignatureUtil.urlPayment +"/init-bill";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        ResponseResultGetToken responseResultGetToken = (ResponseResultGetToken) getToken().getBody();
        String token = responseResultGetToken.getResponse().getToken();

        httpHeaders.add("Authorization" ,"Bearer "+ token);
        HttpEntity<RequestCreateOrder> requestEntity = new HttpEntity<>(request, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        try {
            String input = "";
            validateCreateBill(request);
            request.setCallback_url(SignatureUtil.urlCb);
            request.setMerchant_code(SignatureUtil.merchantCode);
            request.setEmbed_data(SignatureUtil.embedData);

            input+="merchant_code="+request.getMerchant_code();
            input+="&request_id="+request.getRequest_id();
            input+="&amount="+request.getAmount();
            input+="&customer_id="+request.getCustomer_id();
            input+="&payment_method="+request.getPayment_method();
            input+="&embed_data="+request.getEmbed_data();

//            System.out.println("**INPUT_CREATE**: "+input);

            request.setSignature(SignatureUtil.signData(input, SignatureUtil.privateKeyPEM));
//            System.out.println("Signature :"+request.getSignature());
            ResponseEntity<ResponseCreateOrder> result = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    ResponseCreateOrder.class
            );
            log.info("request [{}] response [{}]", gson.toJson(request),gson.toJson(result.getBody()));
            return result;
        }catch (InputMismatchException ex) {
            ResponseMeta meta = new ResponseMeta("400","BAD REQUEST",ex.getMessage());
            ResponseResult response = new ResponseResult(ex.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta,response);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }catch (HttpClientErrorException.Conflict e) {
            ResponseMeta meta = new ResponseMeta("409","CONFLICT","Xảy ra xung đột, đã tồn tại request này");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta,response);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }catch (HttpClientErrorException.Forbidden e) {
            ResponseMeta meta = new ResponseMeta("403","FORBIDDEN","Lỗi token");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta,response);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        } catch (HttpClientErrorException.Unauthorized e) {
            ResponseMeta meta = new ResponseMeta("401","UNAUTHORIZED","Lỗi xác thực chữ ký");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta,response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }catch (Exception e) {

            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> getBillInfo(RequestBillInfo request) {
        final String url = SignatureUtil.urlPayment +"/bill-info";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        ResponseResultGetToken responseResultGetToken = (ResponseResultGetToken) getToken().getBody();
        String token = responseResultGetToken.getResponse().getToken();
        httpHeaders.add("Authorization" ,"Bearer "+token);

        HttpEntity<RequestBillInfo> requestEntity = new HttpEntity<>(request, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        try {
            String input = "";
            validateGetBill(request);
            if(request.getMerchant_code() != null){
                input+="merchant_code="+request.getMerchant_code();
            }
            if(request.getMerchant_order_id()!=null){
                input+="&merchant_order_id="+request.getMerchant_order_id();
            }
            if(request.getGpay_bill_id()!=null){
                input+="&gpay_bill_id="+request.getGpay_bill_id();
            }

//            System.out.println(input);
            request.setSignature(SignatureUtil.signData(input, SignatureUtil.privateKeyPEM));

            ResponseEntity<ResponseGetBill> result = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    ResponseGetBill.class
            );
            log.info("requestGetInfo [{}] responseGetInfo [{}]", gson.toJson(request),gson.toJson(result.getBody()));
            return result;
        }catch (InputMismatchException ex) {
            ResponseMeta meta = new ResponseMeta("400","BAD REQUEST",ex.getMessage());
            ResponseResult response = new ResponseResult(ex.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta,response);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }catch (HttpClientErrorException.NotFound ex) {
            ResponseMeta meta = new ResponseMeta("404","NOT FOUND","Không tìm thấy thông tin thanh toán");
            ResponseResult response = new ResponseResult(ex.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta,response);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }catch (HttpClientErrorException.Conflict e) {
            ResponseMeta meta = new ResponseMeta("409","CONFLICT","Xảy ra xung đột, đã tồn tại request này");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta,response);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }catch (HttpClientErrorException.Forbidden e) {
            ResponseMeta meta = new ResponseMeta("403","FORBIDDEN","Lỗi token");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta,response);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        } catch (HttpClientErrorException.Unauthorized e) {
            ResponseMeta meta = new ResponseMeta("401","UNAUTHORIZED","Lỗi xác thực chữ ký");
            ResponseResult response = new ResponseResult(e.getMessage());
            ResponseCreateOrder result = new ResponseCreateOrder(meta,response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> handleResult(InputWebhook request) {
        try{
            validateWebhook(request);
            String input = "merchant_order_id="+ request.getMerchant_order_id();
            input+= "&gpay_trans_id="+ request.getGpay_trans_id();
            input+= "&gpay_bill_id="+request.getGpay_bill_id();
            input+= "&status="+ request.getStatus();
            if(request.getEmbed_data()!=null && !request.getEmbed_data().isEmpty()){
                input+="&embed_data=" + request.getEmbed_data();
            }
            input+= "&user_payment_method="+ request.getUser_payment_method();
            System.out.println("Input handleResult: "+input);

//            if(!SignatureUtil.signData(input,SignatureUtil.privateKeyPEM).equals(request.getSignature())){
//                throw new InputMismatchException("Chữ ký không hợp lệ !!");
//            }

//            RequestBillInfo requestBillInfo = new RequestBillInfo(SignatureUtil.merchantCode, request.getGpay_bill_id(),request.getMerchant_order_id());
//            ResponseGetBill getBill = (ResponseGetBill) getBillInfo(requestBillInfo).getBody();
//            if(!getBill.getResponse().getSignature().equals(request.getSignature())){
//                throw new InputMismatchException("Chữ ký không hợp lệ !!");
//            }
            if(!SignatureUtil.verifySignature(input,request.getSignature(),SignatureUtil.GpayPublicKey)){
                throw new InputMismatchException("Chữ ký không hợp lệ !!");
            }
            System.out.println("**** Verify Signature Complete !! ****");
            ResultStatusTransaction result = getResultStatusTransaction(request);
            System.out.println("*****"+result.getResult()+"***** 200-OKEY");

            log.info("requestHandleResult [{}] responseHandleResult [{}]", gson.toJson(request),gson.toJson(result));

            return ResponseEntity.ok(result);
        }catch (InputMismatchException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }catch (NullPointerException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    private static ResultStatusTransaction getResultStatusTransaction(InputWebhook request) {
        ResultStatusTransaction result = new ResultStatusTransaction();
        switch (request.getStatus()){
            case "ORDER_PENDING":
                result.setResult("Đơn đặt hàng được bắt đầu và đang chờ xử lý");
                break;
            case "ORDER_PROCESSING":
                result.setResult("Đơn hàng thanh toán đang trong quá trình xử lý");
                break;
            case "ORDER_SUCCESS":
                result.setResult("Đơn hàng thanh toán thành công");
                break;
            case "ORDER_FAILED":
                result.setResult("Đơn hàng thất bại");
                break;
            case "ORDER_CANCEL":
                result.setResult("Đơn hàng bị hủy");
                break;
            case "ORDER_VERIFYING":
                result.setResult("Đơn hàng đang được xác thực");
                break;
            default:
                result.setResult("Trạng thái không xác định");
        }
        result.setStatus(request.getStatus());
        result.setGpay_trans_id(request.getGpay_trans_id());
        result.setGpay_bill_id(request.getGpay_bill_id());
        result.setMerchant_order_id(request.getMerchant_order_id());
        return result;
    }

    public ResponseEntity<?> getToken() {
        final String url = SignatureUtil.urlPayment +"/authentication/token/create";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        RequestGetToken request = new RequestGetToken(SignatureUtil.merchantCode, SignatureUtil.password);
        HttpEntity<RequestGetToken> requestEntity = new HttpEntity<>(request, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        try {


            ResponseEntity<ResponseResultGetToken> result = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    ResponseResultGetToken.class
            );
//            log.info("requestGetToken [{}] responseGetToken [{}]", gson.toJson(request),gson.toJson(result.getBody()));

            return result;
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


    private void validateCreateBill(RequestCreateOrder request) {
        if (request.getRequest_id() == null || request.getRequest_id().isEmpty()) {
            throw new InputMismatchException("request_id can't empty!");
        }
        if (request.getPayment_method() == null || request.getPayment_method().isEmpty()) {
            throw new InputMismatchException("payment_method can't empty!");
        }
        if (request.getAmount() <= 0) {
            throw new InputMismatchException("amount can't <= 0!");
        }
//        if (resource.getWebhook_url() == null || resource.getWebhook_url().isEmpty()
//        || resource.getCallback_url() == null || resource.getCallback_url().isEmpty()
//        || resource.getCustomer_id() == null || resource.getCustomer_id().isEmpty()
//        || resource.getEmbed_data()==null || resource.getEmbed_data().isEmpty()
//        || resource.getPayment_type() == null || resource.getPayment_type().isEmpty())  {
//            throw new InputMismatchException("Nhập thiếu,kiểm tra lại");
//        }
    }

    private void validateGetBill(RequestBillInfo resource) {
        if (resource.getMerchant_code() == null || resource.getMerchant_code().isEmpty()) {
            throw new InputMismatchException("merchant_code can't empty!");
        }
        if (resource.getMerchant_order_id() == null || resource.getMerchant_order_id().isEmpty()) {
            throw new InputMismatchException("merchant_order_id can't empty!");
        }
        if (resource.getGpay_bill_id() == null || resource.getGpay_bill_id().isEmpty()) {
            throw new InputMismatchException("gpay_bill_id can't empty!");
        }
    }

    private void validateWebhook(InputWebhook input){
        if(input.getGpay_trans_id() == null || input.getGpay_trans_id().isEmpty()
        || input.getGpay_bill_id() == null || input.getGpay_bill_id().isEmpty()
        || input.getMerchant_order_id() == null || input.getMerchant_order_id().isEmpty()
        || input.getStatus() == null || input.getStatus().isEmpty()
        || input.getUser_payment_method()==null || input.getUser_payment_method().isEmpty()
        || input.getSignature() == null || input.getSignature().isEmpty()){
            throw new NullPointerException("Thiếu dữ liệu !!");
        }
    }


}
