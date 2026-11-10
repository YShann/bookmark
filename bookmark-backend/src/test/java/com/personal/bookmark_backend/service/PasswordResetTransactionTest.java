package com.personal.bookmark_backend.service;

import com.personal.bookmark_backend.entity.*;
import com.personal.bookmark_backend.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(PasswordResetTransactionTest.Config.class)
@TestPropertySource(properties={"spring.mail.username=test@example.com", "jwt.refresh-token-expiration=2592000000"})
class PasswordResetTransactionTest {
    @Configuration @EnableTransactionManagement
    @EnableJpaRepositories(basePackages="com.personal.bookmark_backend.repository")
    @Import({AuthService.class, RefreshTokenService.class, PasswordResetRateLimiter.class})
    static class Config {
        @Bean DataSource dataSource() { return new DriverManagerDataSource("jdbc:h2:mem:reset;MODE=MySQL;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000", "sa", ""); }
        @Bean LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource ds) {
            var factory = new LocalContainerEntityManagerFactoryBean();
            factory.setDataSource(ds); factory.setPackagesToScan("com.personal.bookmark_backend.entity");
            factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            factory.setJpaPropertyMap(Map.of("hibernate.hbm2ddl.auto", "create-drop"));
            return factory;
        }
        @Bean PlatformTransactionManager transactionManager(jakarta.persistence.EntityManagerFactory emf) { return new JpaTransactionManager(emf); }
        @Bean JavaMailSender mailSender() { return mock(JavaMailSender.class); }
        @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(4); }
        @Bean JwtService jwtService() { return new JwtService(Base64.getEncoder().encodeToString(new byte[32]), 900000); }
    }
    @Autowired AuthService auth;
    @Autowired RefreshTokenService refresh;
    @Autowired UserRepository users;
    @Autowired PasswordResetCodeRepository codes;
    @Autowired PasswordEncoder encoder;
    @Autowired JavaMailSender mail;
    @Autowired PlatformTransactionManager manager;
    Long userId;
    String email;
    BlockingQueue<SimpleMailMessage> messages;
    TransactionTemplate tx;

    @BeforeEach void setup() {
        tx = new TransactionTemplate(manager);
        email = UUID.randomUUID()+"@example.com";
        User user = new User(); user.setEmail(email); user.setPasswordHash(encoder.encode("OldPass123"));
        user.setEmailVerifiedAt(LocalDateTime.now()); userId = users.save(user).getId();
        messages = new LinkedBlockingQueue<>();
        reset(mail);
        doAnswer(call -> { messages.add(call.getArgument(0)); return null; }).when(mail).send(any(SimpleMailMessage.class));
    }
    void seed(String code) {
        tx.executeWithoutResult(status -> {
            var saved = new PasswordResetCode(); saved.setUserId(userId); saved.setCodeHash(encoder.encode(code));
            saved.setExpiresAt(LocalDateTime.now().plusMinutes(5)); codes.save(saved);
        });
    }
    PasswordResetCode saved() { return tx.execute(status -> codes.findByUserId(userId).orElseThrow()); }
    String token() { seed("012345"); return auth.verifyResetCode(email, "012345", email); }

    @Test void registrationUsesConfiguredSenderAndRejectsImmediateResend() {
        String recipient = "new-" + email;
        auth.sendRegistrationCode(recipient);
        assertThrows(com.personal.bookmark_backend.exception.TooManyRequestsException.class,
                () -> auth.sendRegistrationCode(" " + recipient.toUpperCase(Locale.ROOT) + " "));

        SimpleMailMessage message = messages.remove();
        assertEquals("test@example.com", message.getFrom());
        assertArrayEquals(new String[]{recipient}, message.getTo());
        assertTrue(messages.isEmpty());
    }

    @Test void fullFlowRevokesRefreshAndNotifiesWithoutPassword() throws Exception {
        var login = auth.login(email, "OldPass123");
        String token = token();
        assertNotEquals(token, saved().getResetTokenHash());
        assertThrows(IllegalArgumentException.class, () -> auth.verifyResetCode(email,"012345",email));
        auth.resetPassword(token,"NewPass123","NewPass123");
        assertNull(saved().getResetTokenHash());
        assertThrows(IllegalArgumentException.class, () -> auth.resetPassword(token,"OtherPass123","OtherPass123"));
        assertThrows(RuntimeException.class, () -> auth.login(email,"OldPass123"));
        assertNotNull(auth.login(email,"NewPass123").accessToken());
        assertThrows(IllegalArgumentException.class, () -> auth.refreshAccessToken(login.refreshToken()));
        var message = messages.poll(5, TimeUnit.SECONDS); assertNotNull(message);
        assertFalse(message.getText().contains("NewPass123"));
    }
    @Test void fiveFailuresPersistAndBlockCorrectCode() {
        seed("012345");
        for (int i=1; i<=5; i++) {
            assertThrows(IllegalArgumentException.class, () -> auth.verifyResetCode(email,"999999",email));
            assertEquals(i, saved().getAttempts());
        }
        assertThrows(IllegalArgumentException.class, () -> auth.verifyResetCode(email,"012345",email));
        assertNull(saved().getVerifiedAt());
    }
    @Test void expiredCodeAndTokenAreRejected() {
        seed("012345");
        tx.executeWithoutResult(s -> codes.findByUserId(userId).orElseThrow().setExpiresAt(LocalDateTime.now().minusSeconds(1)));
        assertThrows(IllegalArgumentException.class, () -> auth.verifyResetCode(email,"012345",email));
        tx.executeWithoutResult(s -> codes.findByUserId(userId).orElseThrow().setExpiresAt(LocalDateTime.now().plusMinutes(5)));
        String token = auth.verifyResetCode(email,"012345",email);
        tx.executeWithoutResult(s -> codes.findByUserId(userId).orElseThrow().setResetTokenExpiresAt(LocalDateTime.now().minusSeconds(1)));
        assertThrows(IllegalArgumentException.class, () -> auth.resetPassword(token,"NewPass123","NewPass123"));
        assertTrue(encoder.matches("OldPass123", users.findById(userId).orElseThrow().getPasswordHash()));
    }
    @Test void invalidPasswordsDoNotConsumeToken() {
        String token = token();
        assertThrows(IllegalArgumentException.class, () -> auth.resetPassword(token,"short","short"));
        assertThrows(IllegalArgumentException.class, () -> auth.resetPassword(token,"NewPass123","Mismatch123"));
        assertNotNull(saved().getResetTokenHash());
    }
    @Test void concurrentResetHasExactlyOneWinner() throws Exception {
        String token = token();
        var pool = Executors.newFixedThreadPool(2);
        var gate = new CountDownLatch(1);
        Callable<Boolean> task = () -> { gate.await(); try { auth.resetPassword(token,"NewPass123","NewPass123"); return true; } catch (IllegalArgumentException e) { return false; } };
        try {
            var one=pool.submit(task); var two=pool.submit(task); gate.countDown();
            assertNotEquals(one.get(10,TimeUnit.SECONDS), two.get(10,TimeUnit.SECONDS));
        } finally { pool.shutdownNow(); }
        assertNotNull(messages.poll(5,TimeUnit.SECONDS));
    }
    @Test void outerRollbackPreservesPasswordTokenAndRefresh() {
        String token = token(); var login = auth.login(email,"OldPass123");
        assertThrows(IllegalStateException.class, () -> tx.executeWithoutResult(s -> {
            auth.resetPassword(token,"NewPass123","NewPass123"); throw new IllegalStateException("rollback");
        }));
        assertNotNull(saved().getResetTokenHash());
        assertTrue(encoder.matches("OldPass123",users.findById(userId).orElseThrow().getPasswordHash()));
        assertNotNull(auth.refreshAccessToken(login.refreshToken()));
        verifyNoInteractions(mail);
    }
    @Test void resendReplacesExistingRowAndInvalidatesOldToken() throws Exception {
        String oldToken = token(); Long id = saved().getId();
        tx.executeWithoutResult(s -> codes.findByUserId(userId).orElseThrow().setExpiresAt(LocalDateTime.now().plusMinutes(3)));
        auth.forgotPassword(email,email);
        var message=messages.poll(5,TimeUnit.SECONDS); assertNotNull(message);
        PasswordResetCode saved = saved(); assertEquals(id,saved.getId());
        assertNull(saved.getResetTokenHash()); assertNull(saved.getVerifiedAt()); assertEquals(0,saved.getAttempts());
        var matcher=java.util.regex.Pattern.compile("[0-9]{6}").matcher(message.getText()); assertTrue(matcher.find());
        assertTrue(encoder.matches(matcher.group(),saved.getCodeHash()));
        assertThrows(IllegalArgumentException.class, () -> auth.resetPassword(oldToken,"NewPass123","NewPass123"));
        assertThrows(IllegalArgumentException.class, () -> auth.verifyResetCode(email,"012345",email));
        assertNotNull(auth.verifyResetCode(email,matcher.group(),email));
        assertThrows(com.personal.bookmark_backend.exception.TooManyRequestsException.class, () -> auth.forgotPassword(email,email));
    }
    @Test void smtpFailureRollsBackReplacement() throws Exception {
        String token=token();
        tx.executeWithoutResult(s -> codes.findByUserId(userId).orElseThrow().setExpiresAt(LocalDateTime.now().plusMinutes(3)));
        var attempted=new CountDownLatch(1);
        doAnswer(call -> { attempted.countDown(); throw new MailSendException("simulated"); }).when(mail).send(any(SimpleMailMessage.class));
        String previous=saved().getResetTokenHash();
        auth.forgotPassword(email,email); assertTrue(attempted.await(5,TimeUnit.SECONDS));
        assertEquals(previous,saved().getResetTokenHash());
        assertNotNull(token);
    }
    @Test void unknownEmailIsAcceptedAndThrottledLikeKnownEmail() throws Exception {
        String unknown=UUID.randomUUID()+"@example.com";
        auth.forgotPassword(unknown,unknown);
        assertThrows(com.personal.bookmark_backend.exception.TooManyRequestsException.class, () -> auth.forgotPassword(unknown,unknown));
        // 同一背景佇列的任務皆完成後確認未寄信。
        var executor=(ExecutorService)org.springframework.test.util.ReflectionTestUtils.getField(auth,"resetMailExecutor");
        executor.submit(() -> {}).get(5,TimeUnit.SECONDS);
        verifyNoInteractions(mail);
    }
    @Test void concurrentVerificationIssuesOnlyOneToken() throws Exception {
        seed("012345"); var pool=Executors.newFixedThreadPool(2); var gate=new CountDownLatch(1);
        Callable<Boolean> task=() -> { gate.await(); try { auth.verifyResetCode(email,"012345",email); return true; } catch (IllegalArgumentException e) { return false; } };
        try { var one=pool.submit(task); var two=pool.submit(task); gate.countDown();
            assertNotEquals(one.get(10,TimeUnit.SECONDS),two.get(10,TimeUnit.SECONDS));
        } finally { pool.shutdownNow(); }
    }
    @Test void notificationFailureDoesNotUndoReset() throws Exception {
        String token=token(); var attempted=new CountDownLatch(1);
        doAnswer(call -> { attempted.countDown(); throw new MailSendException("simulated"); }).when(mail).send(any(SimpleMailMessage.class));
        auth.resetPassword(token,"NewPass123","NewPass123");
        assertTrue(attempted.await(5,TimeUnit.SECONDS));
        assertNull(saved().getResetTokenHash());
        assertTrue(encoder.matches("NewPass123",users.findById(userId).orElseThrow().getPasswordHash()));
    }
    @Test void concurrentRefreshCannotLeaveUsableOldSessionAfterReset() throws Exception {
        String token=token(); String old=auth.login(email,"OldPass123").refreshToken();
        var pool=Executors.newFixedThreadPool(2); var gate=new CountDownLatch(1);
        try {
            var reset=pool.submit(() -> { gate.await(); auth.resetPassword(token,"NewPass123","NewPass123"); return true; });
            var rotate=pool.submit(() -> { gate.await(); try { return auth.refreshAccessToken(old).refreshToken(); }
                catch (IllegalArgumentException e) { return null; } });
            gate.countDown(); assertTrue(reset.get(10,TimeUnit.SECONDS)); String rotated=rotate.get(10,TimeUnit.SECONDS);
            assertThrows(IllegalArgumentException.class, () -> auth.refreshAccessToken(old));
            if (rotated != null) assertThrows(IllegalArgumentException.class, () -> auth.refreshAccessToken(rotated));
        } finally { pool.shutdownNow(); }
        assertNotNull(messages.poll(5,TimeUnit.SECONDS));
    }
}
