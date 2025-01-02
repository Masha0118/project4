package com.mysite.sbb.recaptcha;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Service
public class RecaptchaService {

    @Value("${recaptcha.secret-key}")
    private String secretKey;

    public boolean verifyCaptchaToken(String token, String clientIP) {
        try {
            String url = "https://www.google.com/recaptcha/api/siteverify";
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            String params = "secret=" + secretKey + "&response=" + token + "&remoteip=" + clientIP;
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(params);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> result = mapper.readValue(response.toString(), Map.class);
                    return Boolean.TRUE.equals(result.get("success"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
