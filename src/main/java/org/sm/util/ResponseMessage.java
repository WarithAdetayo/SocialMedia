package org.sm.util;

public class ResponseMessage<T> {
    private final String message;
    private final int code;
    private final T data;

    public ResponseMessage(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }
    public String getMessage() {
        return  this.message;
    }

    public T getResponseData() {
        return data;
    }

}
