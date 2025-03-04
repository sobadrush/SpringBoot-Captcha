package com.roger.springbootcaptcha.core;

import nl.captcha.text.producer.TextProducer;

import java.util.Random;

/**
 * @author RogerLo
 * @date 2025/3/4
 */
public class NumberTextProducer implements TextProducer {
    private final Random random = new Random();
    private final int length;
    private final char[] chars;

    public NumberTextProducer(int length, String chars) {
        this.length = length;
        this.chars = chars.toCharArray();
    }

    @Override
    public String getText() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(chars[random.nextInt(chars.length)]);
        }
        return result.toString();
    }
}