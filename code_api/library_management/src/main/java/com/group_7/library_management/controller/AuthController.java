package com.group_7.library_management.controller;

import com.group_7.library_management.dto.AuthResponse;
import com.group_7.library_management.dto.BiometricLoginRequest;
import com.group_7.library_management.dto.BiometricTokenResponse;
import com.group_7.library_management.dto.LoginRequest;
import com.group_7.library_management.dto.PasswordChangeCodeRequest;
import com.group_7.library_management.dto.PasswordCodeRequest;
import com.group_7.library_management.dto.PasswordCodeResponse;
import com.group_7.library_management.dto.PasswordResendRequest;
import com.group_7.library_management.dto.PasswordResetRequest;
import com.group_7.library_management.dto.PasswordVerificationRequest;
import com.group_7.library_management.dto.PasswordVerificationResponse;
import com.group_7.library_management.dto.RegisterRequest;
import com.group_7.library_management.dto.RegistrationCodeResponse;
import com.group_7.library_management.dto.ResendRegistrationCodeRequest;
import com.group_7.library_management.dto.UserResponse;
import com.group_7.library_management.dto.UpdateProfileRequest;
import com.group_7.library_management.dto.VerifyRegistrationCodeRequest;
import com.group_7.library_management.service.AuthService;
import com.group_7.library_management.service.PasswordResetService;
import com.group_7.library_management.service.RegistrationVerificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService authService;
    private final RegistrationVerificationService registrationVerificationService;
    private final PasswordResetService passwordResetService;

    public AuthController(
            AuthService authService,
            RegistrationVerificationService registrationVerificationService,
            PasswordResetService passwordResetService
    ) {
        this.authService = authService;
        this.registrationVerificationService = registrationVerificationService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/register/email/code")
    public RegistrationCodeResponse sendRegistrationEmailCode(
            @Valid @RequestBody RegisterRequest request
    ) {
        return registrationVerificationService.sendEmailCode(request);
    }

    @PostMapping("/register/email/resend")
    public RegistrationCodeResponse resendRegistrationEmailCode(
            @Valid @RequestBody ResendRegistrationCodeRequest request
    ) {
        return registrationVerificationService.resendEmailCode(request.registrationId());
    }

    @PostMapping("/register/email/verify")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse verifyRegistrationEmailCode(
            @Valid @RequestBody VerifyRegistrationCodeRequest request
    ) {
        return registrationVerificationService.verifyEmailCode(
                request.registrationId(),
                request.code()
        );
    }

    @PostMapping("/register/phone/code")
    public RegistrationCodeResponse sendRegistrationSmsCode(
            @Valid @RequestBody RegisterRequest request
    ) {
        return registrationVerificationService.sendSmsCode(request);
    }

    @PostMapping("/register/phone/resend")
    public RegistrationCodeResponse resendRegistrationSmsCode(
            @Valid @RequestBody ResendRegistrationCodeRequest request
    ) {
        return registrationVerificationService.resendSmsCode(request.registrationId());
    }

    @PostMapping("/register/phone/verify")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse verifyRegistrationSmsCode(
            @Valid @RequestBody VerifyRegistrationCodeRequest request
    ) {
        return registrationVerificationService.verifySmsCode(
                request.registrationId(),
                request.code()
        );
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {
        return authService.getCurrentUser((Long) authentication.getPrincipal());
    }

    @PatchMapping("/me")
    public UserResponse updateCurrentUser(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return authService.updateCurrentUser(
                (Long) authentication.getPrincipal(),
                request
        );
    }

    @PostMapping("/password/forgot/code")
    public PasswordCodeResponse sendForgotPasswordCode(
            @Valid @RequestBody PasswordCodeRequest request
    ) {
        return passwordResetService.sendForgotPasswordCode(
                request.identifier(),
                request.channel()
        );
    }

    @PostMapping("/password/change/code")
    public PasswordCodeResponse sendChangePasswordCode(
            Authentication authentication,
            @Valid @RequestBody PasswordChangeCodeRequest request
    ) {
        return passwordResetService.sendChangePasswordCode(
                (Long) authentication.getPrincipal(),
                request.channel()
        );
    }

    @PostMapping("/password/code/resend")
    public PasswordCodeResponse resendPasswordCode(
            @Valid @RequestBody PasswordResendRequest request
    ) {
        return passwordResetService.resendCode(request.requestId());
    }

    @PostMapping("/password/code/verify")
    public PasswordVerificationResponse verifyPasswordCode(
            @Valid @RequestBody PasswordVerificationRequest request
    ) {
        return passwordResetService.verifyCode(request.requestId(), request.code());
    }

    @PostMapping("/password/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        passwordResetService.resetPassword(request.resetToken(), request.newPassword());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader("Authorization") String authorization) {
        authService.logout(authorization.substring(BEARER_PREFIX.length()).strip());
    }

    @PostMapping("/biometric/enable")
    public BiometricTokenResponse enableBiometric(Authentication authentication) {
        return authService.enableBiometric((Long) authentication.getPrincipal());
    }

    @PostMapping("/biometric/login")
    public AuthResponse loginWithBiometric(@Valid @RequestBody BiometricLoginRequest request) {
        return authService.loginWithBiometric(request.credential());
    }

    @PostMapping("/biometric/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disableBiometric(Authentication authentication) {
        authService.disableBiometric((Long) authentication.getPrincipal());
    }
}
