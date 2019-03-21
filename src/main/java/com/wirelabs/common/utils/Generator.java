package com.wirelabs.common.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class Generator {
    private static final String DEFAULT_CHARS = "abcdefghijklmnopqrstuvxzywABCDEFGHIJKLMNOPQRSTUVXZYW0123456789";

    Random random = new Random();

    protected String generate(int size, String chars) {
        if (null == chars) {
            chars = DEFAULT_CHARS;
        }
        StringBuilder pass = new StringBuilder();
        for (int i = 0; i < size; i++) {
            pass.append(chars.charAt(random.nextInt(chars.length())));
        }
        return pass.toString();
    }
}
