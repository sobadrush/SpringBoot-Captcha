package com.roger.springbootcaptcha.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author RogerLo
 * @date 2025/3/4
 */
public class UserContext implements AutoCloseable {

    public static final ThreadLocal<String> CAPTCHA_CODE = ThreadLocal.withInitial(() -> null);

    @Override
    public void close() {
        CAPTCHA_CODE.remove();
    }
}