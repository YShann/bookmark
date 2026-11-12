package com.personal.bookmark_backend.controller;

import com.personal.bookmark_backend.config.SecurityConfig;
import com.personal.bookmark_backend.entity.User;
import com.personal.bookmark_backend.exception.*;
import com.personal.bookmark_backend.security.JwtAuthenticationFilter;
import com.personal.bookmark_backend.service.*;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import java.util.Base64;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(AuthControllerSecurityTest.Config.class)
@WebAppConfiguration
@org.springframework.test.context.TestPropertySource(properties = {
        "spring.mail.username=test@example.com"
})
class AuthControllerSecurityTest {
    @Configuration @EnableWebMvc
    @Import({AuthController.class, UserController.class, SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
    static class Config {
        @Bean AuthService authService() { return mock(AuthService.class); }
        @Bean GoogleAuthService googleAuthService() { return mock(GoogleAuthService.class); }
        @Bean UserProfileService userProfileService() { return mock(UserProfileService.class); }
        @Bean AvatarStorageService avatarStorageService() { return mock(AvatarStorageService.class); }
        @Bean JwtService jwtService() { return new JwtService(Base64.getEncoder().encodeToString(new byte[32]),900000); }
        @Bean ObjectMapper objectMapper() { return new JsonMapper(); }
    }
    @Autowired WebApplicationContext context;
    @Autowired @Qualifier("springSecurityFilterChain") Filter security;
    @Autowired AuthService auth;
    @Autowired GoogleAuthService googleAuth;
    @Autowired JwtService jwt;
    MockMvc mvc;
    @BeforeEach void setup() {
        reset(auth, googleAuth);
        mvc=MockMvcBuilders.webAppContextSetup(context).addFilters(security).build();
    }
    @Test void googleLoginIsPublicAndReturnsAppTokens() throws Exception {
        when(googleAuth.login("google-id-token", null))
                .thenReturn(new AuthService.AuthResponse(42L, "user@gmail.com", "access", "refresh"));
        mvc.perform(post("/api/auth/google").contentType("application/json")
                .content("{\"idToken\":\"google-id-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(42))
                .andExpect(jsonPath("$.accessToken").value("access"))
                .andExpect(jsonPath("$.refreshToken").value("refresh"));
        verify(googleAuth).login("google-id-token", null);
    }

    @Test void persistentSessionCanRenewWithoutAccessTokenAndSignalsRevocation() throws Exception {
        when(auth.refreshSession("refresh"))
                .thenReturn(new AuthService.AuthResponse(42L, "user@gmail.com", "access", "refresh"))
                .thenThrow(new UnauthorizedException("invalid session"));
        var request = post("/api/auth/refresh-session").contentType("application/json")
                .content("{\"refreshToken\":\"refresh\"}");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access"))
                .andExpect(jsonPath("$.refreshToken").value("refresh"));
        mvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("X-Bookmark-Auth-Action", "sign-in"));
    }

    @Test void googleLoginAcceptsPasswordWhenConfirmingLink() throws Exception {
        when(googleAuth.login("google-id-token", "CurrentPassword123"))
                .thenReturn(new AuthService.AuthResponse(42L, "user@gmail.com", "access", "refresh"));
        mvc.perform(post("/api/auth/google").contentType("application/json")
                .content("{\"idToken\":\"google-id-token\",\"password\":\"CurrentPassword123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(42));
        verify(googleAuth).login("google-id-token", "CurrentPassword123");
    }

    @Test void googleLoginConflictSignalsWhenItNeedsPasswordConfirmation() throws Exception {
        when(googleAuth.login("google-id-token", null)).thenThrow(new GoogleLinkRequiredException());
        mvc.perform(post("/api/auth/google").contentType("application/json")
                .content("{\"idToken\":\"google-id-token\"}"))
                .andExpect(status().isConflict())
                .andExpect(header().string("X-Bookmark-Auth-Action", "link-google"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test void googleLoginRejectsMissingTokenBeforeCallingService() throws Exception {
        mvc.perform(post("/api/auth/google").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(googleAuth);
    }

    @Test void googleAccountChangesRequireAnAppSession() throws Exception {
        mvc.perform(post("/api/users/me/google")).andExpect(status().isUnauthorized());
        verifyNoInteractions(googleAuth);
    }

    @Test void linkUsesUserIdFromTheAuthenticatedSession() throws Exception {
        var user = new User();
        user.setEmail("user@gmail.com");
        user.setGoogleSubject("google-user");
        user.setPasswordHash("hash");
        when(googleAuth.link(42L, "google-id-token", "password")).thenReturn(user);
        mvc.perform(post("/api/users/me/google")
                .header("Authorization", "Bearer " + jwt.generateAccessToken(42L))
                .contentType("application/json")
                .content("{\"idToken\":\"google-id-token\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.googleLinked").value(true));
        verify(googleAuth).link(42L, "google-id-token", "password");
    }
    @Test void forgotPasswordIsPublicAndUsesConnectionAddress() throws Exception {
        mvc.perform(post("/api/auth/forgot-password").contentType("application/json")
                .header("X-Forwarded-For","203.0.113.99")
                .with(request -> { request.setRemoteAddr("192.0.2.1"); return request; })
                .content("{\"email\":\"user@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("如果此 Email 已註冊，將會收到密碼重設驗證碼"));
        verify(auth).forgotPassword("user@example.com","192.0.2.1");
    }
    @Test void verifyCodeIsPublicAndPreservesLeadingZero() throws Exception {
        when(auth.verifyResetCode("user@example.com","012345","127.0.0.1")).thenReturn("opaque-reset-token");
        mvc.perform(post("/api/auth/verify-reset-code").contentType("application/json")
                .content("{\"email\":\"user@example.com\",\"code\":\"012345\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.resetToken").value("opaque-reset-token"));
        verify(auth).verifyResetCode("user@example.com","012345","127.0.0.1");
    }
    @Test void resetIsPublicAndReturnsMessageOnly() throws Exception {
        mvc.perform(post("/api/auth/reset-password").contentType("application/json")
                .content("{\"resetToken\":\"opaque\",\"newPassword\":\"NewPass123\",\"confirmPassword\":\"NewPass123\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("密碼重設成功，請重新登入"))
                .andExpect(jsonPath("$.accessToken").doesNotExist());
        verify(auth).resetPassword("opaque","NewPass123","NewPass123");
    }
    @Test void invalidRequestsNeverReachService() throws Exception {
        String[] routes={"forgot-password","verify-reset-code","reset-password"};
        String[] bodies={"{\"email\":\"invalid\"}","{\"email\":\"user@example.com\",\"code\":\"12345\"}","{\"resetToken\":\"\",\"newPassword\":\"NewPass123\",\"confirmPassword\":\"NewPass123\"}"};
        for(int i=0;i<routes.length;i++) mvc.perform(post("/api/auth/"+routes[i]).contentType("application/json").content(bodies[i]))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        verifyNoInteractions(auth);
    }
    @Test void rateLimitUsesExistingErrorEnvelope() throws Exception {
        doThrow(new TooManyRequestsException()).when(auth).forgotPassword(anyString(),anyString());
        mvc.perform(post("/api/auth/forgot-password").contentType("application/json").content("{\"email\":\"user@example.com\"}"))
                .andExpect(status().isTooManyRequests()).andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.message").value("請求過於頻繁，請稍後再試"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    @Test void invalidResetTokenIsReportedByService() throws Exception {
        doThrow(new IllegalArgumentException("重設密碼憑證無效或已過期，請重新驗證"))
                .when(auth).resetPassword(anyString(),anyString(),anyString());
        mvc.perform(post("/api/auth/reset-password").contentType("application/json")
                .content("{\"resetToken\":\"invalid\",\"newPassword\":\"NewPass123\",\"confirmPassword\":\"NewPass123\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }
    @Test void otherPathsAndMethodsRemainProtected() throws Exception {
        mvc.perform(post("/api/auth/logout-all")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/bookmarks")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/auth/forgot-password")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/forgot-password/extra")).andExpect(status().isUnauthorized());
        verifyNoInteractions(auth);
    }
    @Test void validJwtStillAuthenticatesProtectedEndpoint() throws Exception {
        mvc.perform(post("/api/auth/logout-all").header("Authorization","Bearer "+jwt.generateAccessToken(42L)))
                .andExpect(status().isOk());
        verify(auth).logoutAll(42L);
    }
    @Test void invalidJwtCannotAuthenticateProtectedEndpoint() throws Exception {
        mvc.perform(post("/api/auth/logout-all").header("Authorization","Bearer invalid"))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.status").value(401));
        verifyNoInteractions(auth);
    }
}
