package com.mysite.sbb.user;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class EncryptionService {

    private SecretKey secretKey;

    @Value("${encryption.secret-key}")
    private String encodedKey; // 암호화된 형태로 키를 환경 변수에 저장

    @PostConstruct
    public void init() {
        // 환경 변수에서 키를 복호화하여 SecretKey 객체로 변환
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        this.secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public String encrypt(String data) throws Exception {
        return AESUtil.encrypt(data, secretKey);
    }

    public String decrypt(String encryptedData) throws Exception {
        return AESUtil.decrypt(encryptedData, secretKey);
    }
}
