package com.example.demo;

import lombok.Getter;
import lombok.Setter;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Getter
@Setter
public class SignatureUtil {
    final public static String urlPayment = "https://payment.sandbox.g-pay.vn/api/v4";
    final public static String urlMpa = "https://mpa.sandbox.g-pay.vn/api/v3";
    final public static String urlVirtualAccount = "https://mpa-va.sandbox.g-pay.vn/api/v3/";

    final public static String merchantCode = "MC001";
    final public static String password = "123456aA@";
    final public static String embedData = "embed_data";
    final public static String urlCb = "https://google.com";

    final public static String privateKeyPEM =
                    "ahihi";
    final public static String privateKeyPKS8 =
                    "ahihi1";

    final public static String publicKeyPEM =
                    "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDAYFXDXlSBqhMrBn0I1+2" +
                    "SczoAt49V+CD22drlF4zXQJCy8InTr763aHw8Fj+rJlZEevqLfqzDVhYCfaPZqnigm+GbyZQ/jBKZ3tIuKNqbQPZf" +
                    "JcxlhWhPWts5QX69blRAWKjPOocm2r5Y+pYPmrvj9YbRcaHi19rMjE8zFm39PQIDAQAB";

    final public static String GpayPublicKey =
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0cNJLuLV75oN3PEHTsxM" +
                    "UEm0NLJq77oDwliN8rNlubvFn01njMd5e669GzTNbNYMropGjksefC6AnLwT94DV" +
                    "Hj/xB5LxU6Dp5sBXRTPqWXWXcnI5hvdqx5VZxZCKkqUBrp7JOV40wcRkicef9F5H" +
                    "pzHli/wM54yXhnQkKdyZpzgmr9lKQRIEnUCq+rMg1wNom0hIAHdUO1QsXywG/7oR" +
                    "yyfE0YJiETHrJjW7zWO7aAa97O7BwV6wGHbOKYVBMG7gCuWkbiJE42kC2xmQS3Il" +
                    "1v47+UC4Dg2ly9ow++k9uVLRoPKJx6Dt1U9+F1i11HNyJIKBkbxI1TJZzY93HocZfwIDAQAB";

    public static String signData(String input, String privateKeyPEM) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);

        // Khởi tạo BouncyCastleProvider
        Security.addProvider(new BouncyCastleProvider());

        // Chuyển đổi khóa thành PrivateKey
        RSAPrivateKey rsaPrivateKey = RSAPrivateKey.getInstance(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(rsaPrivateKey.getEncoded()));

        // Khởi tạo chữ ký
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(input.getBytes());

        // Ký dữ liệu
        byte[] digitalSignature = signature.sign();

        // Mã hóa chữ ký thành Base64
        return Base64.getEncoder().encodeToString(digitalSignature);
    }

    // Ký sử dụng private key là #PKCS8
    public static String signDataV2(String input, String base64PrivateKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(input.getBytes(StandardCharsets.UTF_8));

        byte[] binarySignature = signature.sign();
        return Base64.getEncoder().encodeToString(binarySignature);
    }

    // Xác thực chữ ký
    public static boolean verifySignature(String input, String signatureBase64, String publicKeyPEM) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(input.getBytes());

        byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);

        return signature.verify(signatureBytes);
    }

    public static void main(String[] args) {
        try {
            String input =
                    "merchant_code=MC001&from_date=20241021&to_date=20241026&page=2&per_page=10";

            String s1 = signData(input,privateKeyPEM);
            String s2 = signDataV2(input,privateKeyPKS8);

            System.out.println(s1);
            System.out.println(s2);

            System.out.println(s1.equals(s2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
