package com.group_7.library_management.service;

import com.group_7.library_management.dto.RegisterRequest;
import com.group_7.library_management.dto.RegistrationCodeResponse;
import com.group_7.library_management.dto.UserResponse;
import com.group_7.library_management.entity.RegistrationVerification;
import com.group_7.library_management.entity.User;
import com.group_7.library_management.entity.VerificationChannel;
import com.group_7.library_management.exception.ConflictException;
import com.group_7.library_management.exception.EmailDeliveryException;
import com.group_7.library_management.exception.SmsDeliveryException;
import com.group_7.library_management.exception.UnauthorizedException;
import com.group_7.library_management.repository.RegistrationVerificationRepository;
import com.group_7.library_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

@Service
public class RegistrationVerificationService {

    private final RegistrationVerificationRepository verificationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegistrationEmailService emailService;
    private final RegistrationSmsService smsService;
    private final Duration expiration;
    private final int maxAttempts;
    private final SecureRandom secureRandom = new SecureRandom();

    public RegistrationVerificationService(
            RegistrationVerificationRepository verificationRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RegistrationEmailService emailService,
            RegistrationSmsService smsService,
            @Value("${app.registration-otp.expiration:PT5M}") Duration expiration,
            @Value("${app.registration-otp.max-attempts:5}") int maxAttempts
    ) {
        this.verificationRepository = verificationRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.smsService = smsService;
        this.expiration = expiration;
        this.maxAttempts = maxAttempts;
    }

    @Transactional
    public RegistrationCodeResponse sendEmailCode(RegisterRequest request) {
        return sendCode(request, VerificationChannel.EMAIL);
    }

    @Transactional
    public RegistrationCodeResponse sendSmsCode(RegisterRequest request) {
        return sendCode(request, VerificationChannel.SMS);
    }

    private RegistrationCodeResponse sendCode(
            RegisterRequest request,
            VerificationChannel channel
    ) {
        String fullName = normalizeFullName(request.fullName());
        String email = request.email().strip().toLowerCase(Locale.ROOT);
        String phone = request.phone().strip();
        ensureAccountDoesNotExist(email, phone);

        verificationRepository.deleteByEmailIgnoreCaseOrPhone(email, phone);

        String code = generateCode();
        RegistrationVerification verification = new RegistrationVerification(
                fullName,
                email,
                phone,
                passwordEncoder.encode(request.password()),
                passwordEncoder.encode(code),
                Instant.now().plus(expiration),
                channel
        );
        verificationRepository.save(verification);

        try {
            deliverCode(verification, code);
        } catch (EmailDeliveryException | SmsDeliveryException exception) {
            verificationRepository.delete(verification);
            throw exception;
        }

        return responseFor(verification);
    }

    @Transactional
    public RegistrationCodeResponse resendEmailCode(String registrationId) {
        return resendCode(registrationId, VerificationChannel.EMAIL);
    }

    @Transactional
    public RegistrationCodeResponse resendSmsCode(String registrationId) {
        return resendCode(registrationId, VerificationChannel.SMS);
    }

    private RegistrationCodeResponse resendCode(
            String registrationId,
            VerificationChannel expectedChannel
    ) {
        RegistrationVerification verification = findVerification(registrationId);
        ensureChannel(verification, expectedChannel);
        ensureAccountDoesNotExist(verification.getEmail(), verification.getPhone());

        String code = generateCode();
        verification.renewCode(
                passwordEncoder.encode(code),
                Instant.now().plus(expiration)
        );
        verificationRepository.save(verification);
        deliverCode(verification, code);
        return responseFor(verification);
    }

    @Transactional
    public UserResponse verifyEmailCode(String registrationId, String code) {
        return verifyCode(registrationId, code, VerificationChannel.EMAIL);
    }

    @Transactional
    public UserResponse verifySmsCode(String registrationId, String code) {
        return verifyCode(registrationId, code, VerificationChannel.SMS);
    }

    private UserResponse verifyCode(
            String registrationId,
            String code,
            VerificationChannel expectedChannel
    ) {
        RegistrationVerification verification = findVerification(registrationId);
        ensureChannel(verification, expectedChannel);

        if (verification.getExpiresAt().isBefore(Instant.now())) {
            verificationRepository.delete(verification);
            throw new UnauthorizedException("Mã xác nhận đã hết hạn. Vui lòng đăng ký lại");
        }

        if (verification.getFailedAttempts() >= maxAttempts) {
            verificationRepository.delete(verification);
            throw new UnauthorizedException("Bạn đã nhập sai mã quá nhiều lần. Vui lòng đăng ký lại");
        }

        if (!passwordEncoder.matches(code, verification.getCodeHash())) {
            verification.recordFailedAttempt();
            verificationRepository.save(verification);
            throw new UnauthorizedException("Mã xác nhận không chính xác");
        }

        ensureAccountDoesNotExist(verification.getEmail(), verification.getPhone());
        User user = new User(
                verification.getFullName(),
                verification.getEmail(),
                verification.getPhone(),
                verification.getPasswordHash()
        );
        User savedUser = userRepository.save(user);
        verificationRepository.delete(verification);
        return UserResponse.from(savedUser);
    }

    private RegistrationVerification findVerification(String registrationId) {
        return verificationRepository.findById(registrationId)
                .orElseThrow(() -> new UnauthorizedException(
                        "Yêu cầu xác nhận không tồn tại hoặc đã hết hạn"
                ));
    }

    private void ensureAccountDoesNotExist(String email, String phone) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email đã được sử dụng");
        }
        if (userRepository.existsByPhone(phone)) {
            throw new ConflictException("Số điện thoại đã được sử dụng");
        }
    }

    private RegistrationCodeResponse responseFor(RegistrationVerification verification) {
        return new RegistrationCodeResponse(
                verification.getId(),
                verification.getVerificationChannel() == VerificationChannel.EMAIL
                        ? maskEmail(verification.getEmail())
                        : maskPhone(verification.getPhone()),
                expiration.toSeconds()
        );
    }

    private void deliverCode(RegistrationVerification verification, String code) {
        if (verification.getVerificationChannel() == VerificationChannel.EMAIL) {
            emailService.sendRegistrationCode(verification.getEmail(), code);
        } else {
            smsService.sendRegistrationCode(verification.getPhone(), code);
        }
    }

    private void ensureChannel(
            RegistrationVerification verification,
            VerificationChannel expectedChannel
    ) {
        if (verification.getVerificationChannel() != expectedChannel) {
            throw new UnauthorizedException("Phương thức xác nhận không đúng");
        }
    }

    private String generateCode() {
        return String.format(Locale.ROOT, "%06d", secureRandom.nextInt(1_000_000));
    }

    private String normalizeFullName(String fullName) {
        return fullName.strip().replaceAll("\\s+", " ");
    }

    private String maskEmail(String email) {
        int separator = email.indexOf('@');
        if (separator <= 1) {
            return "***" + email.substring(Math.max(separator, 0));
        }
        return email.charAt(0) + "***" + email.substring(separator);
    }

    private String maskPhone(String phone) {
        if (phone.length() <= 4) {
            return "****";
        }
        return "*".repeat(phone.length() - 4) + phone.substring(phone.length() - 4);
    }
}
