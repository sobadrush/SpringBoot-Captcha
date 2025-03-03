package com.roger.springbootcaptcha;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

@SpringBootApplication
public class SpringBootCaptchaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootCaptchaApplication.class, args);
    }

    @Bean
    public Config kaptchaConfig() {
        Properties properties = new Properties();
        properties.put("kaptcha.border", "no");
        // properties.put("kaptcha.border.color", "105,179,90");
        properties.put("kaptcha.textproducer.font.color", "blue");
        properties.put("kaptcha.image.width", "120");
        properties.put("kaptcha.image.height", "40");
        properties.put("kaptcha.textproducer.font.size", "27");
        properties.put("kaptcha.session.key", "code");
        properties.put("kaptcha.textproducer.char.length", "4"); // 驗證碼字元長度
        // properties.put("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
        properties.put("kaptcha.textproducer.char.string", "0123456789"); // 驗證碼文字集合
        // properties.put("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.WaterRipple"); // 驗證碼干擾類(水纹)
        // properties.put("kaptcha.obscurificator.impl", "com.roger.springbootcaptcha.core.NoDistortion"); // 驗證碼干擾類(無干擾)

        properties.put("kaptcha.noise.color", "black"); // 干擾的顏色
        // properties.put("kaptcha.noise.impl", "com.google.code.kaptcha.impl.DefaultNoise"); // 干擾實現類 (NoNoise/DefaultNoise/ShadowGimpy/FishEyeGimpy/WaterRipple)
        properties.put("kaptcha.noise.impl", "com.roger.springbootcaptcha.core.RandomLineNoise"); // 干擾實現類 (自訂類別)
        properties.put("kaptcha.background.clear.from", "204,237,254"); // 背景顏色漸變，開始顏色
        properties.put("kaptcha.background.clear.to", "204,237,254"); // 背景顏色漸變，結束顏色
        properties.put("kaptcha.textproducer.char.space", "8"); // 字元間隔
        return new Config(properties);
    }

    @Bean
    public DefaultKaptcha captchaProducer(Config kaptchaConfig) {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(kaptchaConfig);
        return defaultKaptcha;
    }

}
