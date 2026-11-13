package com.personal.bookmark_backend.service;

import com.personal.bookmark_backend.exception.TooManyRequestsException;

import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetRateLimiterTest {
    static class TestClock extends Clock {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");

        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        public Clock withZone(ZoneId zone) {
            return this;
        }

        public Instant instant() {
            return now;
        }
    }

    @Test
    void cooldownAndAccountHourlyLimitApplyAcrossSources() {
        var clock = new TestClock();
        var limiter = new PasswordResetRateLimiter(clock);
        limiter.check("send", "account", "ip1");
        assertThrows(TooManyRequestsException.class, () -> limiter.check("send", "account", "ip2"));
        for (int i = 1; i < 5; i++) {
            clock.now = clock.now.plusSeconds(60);
            limiter.check("send", "account", "ip" + i);
        }
        clock.now = clock.now.plusSeconds(60);
        assertThrows(TooManyRequestsException.class, () -> limiter.check("send", "account", "another"));
        clock.now = clock.now.plusSeconds(3600);
        assertDoesNotThrow(() -> limiter.check("send", "account", "ip1"));
    }

    @Test
    void sourceLimitAppliesAcrossAccounts() {
        var limiter = new PasswordResetRateLimiter(new TestClock());
        for (int i = 0; i < 20; i++) limiter.check("send", "account" + i, "same-ip");
        assertThrows(TooManyRequestsException.class, () -> limiter.check("send", "new-account", "same-ip"));
    }

    @Test
    void registrationAndResetHaveSeparateSixtySecondCooldowns() {
        var clock = new TestClock();
        var limiter = new PasswordResetRateLimiter(clock);

        limiter.checkRegistrationResend("account");
        limiter.check("send", "account", "ip1");
        clock.now = clock.now.plusMillis(59_999);
        assertThrows(TooManyRequestsException.class, () -> limiter.checkRegistrationResend("account"));
        clock.now = clock.now.plusMillis(1);
        assertDoesNotThrow(() -> limiter.checkRegistrationResend("account"));
    }
}
