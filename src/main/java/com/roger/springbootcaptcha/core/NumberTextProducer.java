package com.roger.springbootcaptcha.core;

import nl.captcha.text.producer.TextProducer;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author RogerLo
 * @date 2025/3/4
 */
public class NumberTextProducer implements TextProducer {
    private final Random random = new SecureRandom();
    private final int length;
    private final char[] chars;

    public NumberTextProducer(int length, String chars) {
        this.length = length;
        this.chars = chars.toCharArray();
    }

    @Override
    public String getText() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sb.append(chars[random.nextInt(chars.length)]);
        }
        return sb.toString();
    }
}