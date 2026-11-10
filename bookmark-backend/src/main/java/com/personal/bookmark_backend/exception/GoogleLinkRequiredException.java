package com.personal.bookmark_backend.exception;

public class GoogleLinkRequiredException extends ConflictException {
    public GoogleLinkRequiredException() {
        super("此 Email 已有帳號，請輸入原本的 App 密碼來綁定 Google");
    }
}
