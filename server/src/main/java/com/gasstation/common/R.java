package com.gasstation.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一接口返回体
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int CODE_OK = 0;
    public static final int CODE_UNAUTHORIZED = 401;
    public static final int CODE_FORBIDDEN = 403;
    public static final int CODE_ERROR = 500;

    private int code;
    private String msg;
    private T data;

    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setCode(CODE_OK);
        r.setMsg("ok");
        r.setData(data);
        return r;
    }

    public static <T> R<T> fail(String msg) {
        return fail(CODE_ERROR, msg);
    }

    public static <T> R<T> fail(int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }
}
