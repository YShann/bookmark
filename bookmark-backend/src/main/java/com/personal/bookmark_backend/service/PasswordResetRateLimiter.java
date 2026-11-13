package com.personal.bookmark_backend.service;

import com.personal.bookmark_backend.exception.TooManyRequestsException;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

/**
 * 單一 JVM 的有界限流；多副本部署須改用共享限流儲存。
 */
@Component
public class PasswordResetRateLimiter {
    static final int RESEND_SECONDS = 60;

    private final Clock clock;
    private final Map<String, Window> windows = new HashMap<>();

    public PasswordResetRateLimiter() {
        this(Clock.systemUTC());
    }

    PasswordResetRateLimiter(Clock clock) {
        this.clock = clock;
    }

    public synchronized void check(String action, String accountHash, String source) {
        long now = startCheck();
        boolean send = action.equals("send");
        // 來源計數也涵蓋被帳號節流拒絕的請求。
        consume(
                action + ":source:" + (source == null ? "unknown" : source), now,
                send ? 20 : 100, 3600000L
        );
        if (send) consume("cooldown:" + accountHash, now, 1, RESEND_SECONDS * 1000L);
        consume(action + ":account:" + accountHash, now, send ? 5 : 20, 3600000L);
    }

    public synchronized void checkRegistrationResend(String accountHash) {
        consume("registration:cooldown:" + accountHash, startCheck(), 1, RESEND_SECONDS * 1000L);
    }

    private long startCheck() {
        long now = clock.millis();
        windows.entrySet().removeIf(entry -> entry.getValue().until <= now);
        if (windows.size() >= 20000) throw new TooManyRequestsException();
        return now;
    }

    private void consume(String key, long now, int limit, long duration) {
        Window window = windows.computeIfAbsent(key, ignored -> new Window(now + duration));
        if (window.count >= limit) throw new TooManyRequestsException();
        window.count++;
    }

    private static class Window {
        final long until;
        int count;

        Window(long until) {
            this.until = until;
        }
    }
}
