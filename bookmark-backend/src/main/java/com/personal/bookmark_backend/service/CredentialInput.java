package com.personal.bookmark_backend.service;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

final class CredentialInput {
    private static final Pattern SESSION_TOKEN = Pattern.compile("[A-Za-z0-9_-]{43}");

    private CredentialInput() {}

    static boolean isSessionToken(String value) {
        return value != null && SESSION_TOKEN.matcher(value).matches();
    }

    static boolean fitsBcrypt(String password) {
        return password != null && password.length() <= 72
                && password.getBytes(StandardCharsets.UTF_8).length <= 72;
    }
}
