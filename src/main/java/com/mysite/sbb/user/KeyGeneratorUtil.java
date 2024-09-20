package com.mysite.sbb.user;

import javax.crypto.SecretKey;
import java.util.Base64;

public class KeyGeneratorUtil {
    public static void main(String[] args) throws Exception {
        SecretKey key = AESUtil.generateKey(128);
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("Base64 Encoded Key: " + base64Key);
    }
}
