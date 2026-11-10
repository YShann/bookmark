package com.personal.bookmark_backend.exception;

public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException() {
        super("請求過於頻繁，請稍後再試");
    }
}
