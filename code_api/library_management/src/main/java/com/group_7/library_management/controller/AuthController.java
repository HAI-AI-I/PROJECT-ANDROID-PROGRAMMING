package com.group_7.library_management.controller;

import com.group_7.library_management.dto.AuthResponse;
import com.group_7.library_management.dto.BiometricLoginRequest;
import com.group_7.library_management.dto.BiometricTokenResponse;
import com.group_7.library_management.dto.LoginRequest;
import com.group_7.library_management.dto.RegisterRequest;
import com.group_7.library_management.dto.RegistrationCodeResponse;
import com.group_7.library_management.dto.ResendRegistrationCodeRequest;
import com.group_7.library_management.dto.UserResponse;
import com.group_7.library_management.dto.VerifyRegistrationCodeRequest;
import com.group_7.library_management.service.AuthService;
import com.group_7.library_management.service.RegistrationVerificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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

    public AuthController(
            AuthService authService,
            RegistrationVerificationService registrationVerificationService
    ) {
        this.authService = authService;
        this.registrationVerificationService = registrationVerificationService;
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
