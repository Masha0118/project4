package com.mysite.sbb.recaptcha;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RecaptchaConfig {

    @Value("${recaptcha.site-key}")
    private String siteKey;

    @Value("${recaptcha.secret-key}")
    private String secretKey;


}
