package com.eg.yaima.common;

public enum UserStatus {

    ONLINE("ONL"),
    OFFLINE("OFF");

    private final String code;

    UserStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
