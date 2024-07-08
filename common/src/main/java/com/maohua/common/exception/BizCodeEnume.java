package com.maohua.common.exception;

public enum BizCodeEnume {
    UNKNOWN_EXCEPTION(10000, "UNKNOWN EXCEPTION"),
    VALID_EXCEPTION(10001, "DATA VALIDATION FAILED"),
    PRODUCT_UP_EXCEPTION(11000, "PRODUCT UP ERROR");


    private int code;
    private String msg;
    BizCodeEnume(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
}
