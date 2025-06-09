package com.eg.yaima;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Constant {

    public static final int MAX_USERNAME_LEN = 25;
    public static final Charset CHARSET = StandardCharsets.US_ASCII;

    public enum WINDOW {
        LOGIN,
        CURRENT, MAIN
    }
}
