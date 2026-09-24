package com.group_7.library_management.service;

import com.group_7.library_management.dto.PasswordCodeResponse;
import com.group_7.library_management.dto.PasswordVerificationResponse;
import com.group_7.library_management.entity.PasswordResetVerification;
import com.group_7.library_management.entity.User;
import com.group_7.library_management.entity.VerificationChannel;
import com.group_7.library_management.exception.BadRequestException;
import com.group_7.library_management.exception.ResourceNotFoundException;
import com.group_7.library_management.exception.UnauthorizedException;
import com.group_7.library_management.repository.PasswordResetVerificationRepository;
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
public class PasswordResetService {

    private final PasswordResetVerificationRepository verificationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegistrationEmailService emailService;
    private final RegistrationSmsService smsService;
    private final TokenService tokenService;
    private final Duration codeExpiration;
    private final Duration resetExpiration;
    private final int maxAttempts;
    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetService(
            PasswordResetVerificationRepository verificationRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RegistrationEmailService emailService,
            RegistrationSmsService smsService,
            TokenService tokenService,
            @Value("${app.password-reset.code-expiration:PT5M}") Duration codeExpiration,
            @Value("${app.password-reset.reset-expiration:PT10M}") Duration resetExpiration,
            @Value("${app.password-reset.max-attempts:5}") int maxAttempts
    ) {
        this.verificationRepository = verificationRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.smsService = smsService;
        this.tokenService = tokenService;
        this.codeExpiration = codeExpiration;
        this.resetExpiration = resetExpiration;
        this.maxAttempts = maxAttempts;
    }

    @Transactional
    public PasswordCodeResponse sendForgotPasswordCode(
            String identifier,
            VerificationChannel channel
    ) {
        String normalized = identifier.strip().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmailIgnoreCaseOrPhone(normalized, normalized)
                .filter(User::isActive)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy tài khoản phù hợp với thông tin đã nhập"
                ));
        ensureIdentifierMatchesChannel(user, normalized, channel);
        return createAndSend(user, channel);
    }

    @Transactional
    public PasswordCodeResponse sendChangePasswordCode(
            Long userId,
            VerificationChannel channel
    ) {
        User user = userRepository.findById(userId)
                .filter(User::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        return createAndSend(user, channel);
    }

    @Transactional
    public PasswordCodeResponse resendCode(String requestId) {
        PasswordResetVerification verification = findRequest(requestId);
        if (verification.isVerified()) {
            throw new BadRequestException("Mã đã được xác nhận, không thể gửi lại");
        }
        String code = generateCode();
        verification.renewCode(passwordEncoder.encode(code), Instant.now().plus(codeExpiration));
        verificationRepository.save(verification);
        deliverCode(verification.getUser(), verification.getChannel(), code);
        return responseFor(verification);
    }

    @Transactional
    public PasswordVerificationResponse verifyCode(String requestId, String code) {
        PasswordResetVerification verification = findRequest(requestId);
        if (verification.isVerified()) {
            throw new BadRequestException("Mã xác nhận đã được sử dụng");
        }
        ensureNotExpired(verification, "Mã xác nhận đã hết hạn. Vui lòng gửi lại mã mới");
        if (verification.getFailedAttempts() >= maxAttempts) {
            verificationRepository.delete(verification);
            throw new UnauthorizedException("Bạn đã nhập sai mã quá nhiều lần. Vui lòng yêu cầu mã mới");
        }
        if (!passwordEncoder.matches(code, verification.getCodeHash())) {
            verification.recordFailedAttempt();
            verificationRepository.save(verification);
            throw new UnauthorizedException("Mã xác nhận không chính xác");
        }
        verification.markVerified(Instant.now().plus(resetExpiration));
        verificationRepository.save(verification);
        return new PasswordVerificationResponse(verification.getId());
    }

    @Transactional
    public void resetPassword(String resetToken, String newPassword) {
        PasswordResetVerification verification = findRequest(resetToken);
        if (!verification.isVerified()) {
            throw new UnauthorizedException("Bạn chưa xác nhận mã OTP");
        }
        ensureNotExpired(verification, "Phiên đặt lại mật khẩu đã hết hạn");
        User user = verification.getUser();
        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new BadRequestException("Mật khẩu mới không được trùng với mật khẩu hiện tại");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenService.revokeAllUserTokens(user.getId());
        verificationRepository.delete(verification);
    }

    private PasswordCodeResponse createAndSend(User user, VerificationChannel channel) {
        verificationRepository.deleteAllByUserId(user.getId());
        String code = generateCode();
        PasswordResetVerification verification = new PasswordResetVerification(
                user,
                channel,
                passwordEncoder.encode(code),
                Instant.now().plus(codeExpiration)
        );
        verificationRepository.save(verification);
        deliverCode(user, channel, code);
        return responseFor(verification);
    }

    private void deliverCode(User user, VerificationChannel channel, String code) {
        if (channel == VerificationChannel.EMAIL) {
            emailService.sendPasswordResetCode(user.getEmail(), code);
        } else {
            smsService.sendPasswordResetCode(user.getPhone(), code);
        }
    }

    private void ensureIdentifierMatchesChannel(
            User user,
            String identifier,
            VerificationChannel channel
    ) {
        boolean matches = channel == VerificationChannel.EMAIL
                ? user.getEmail().equalsIgnoreCase(identifier)
                : user.getPhone().equals(identifier);
        if (!matches) {
            throw new BadRequestException(
                    channel == VerificationChannel.EMAIL
                            ? "Vui lòng nhập email của tài khoản"
                            : "Vui lòng nhập số điện thoại của tài khoản"
            );
        }
    }

    private PasswordResetVerification findRequest(String requestId) {
        return verificationRepository.findById(requestId)
                .orElseThrow(() -> new UnauthorizedException(
                        "Yêu cầu thay đổi mật khẩu không tồn tại hoặc đã hết hạn"
                ));
    }

    private void ensureNotExpired(PasswordResetVerification verification, String message) {
        if (verification.getExpiresAt().isBefore(Instant.now())) {
            verificationRepository.delete(verification);
            throw new UnauthorizedException(message);
        }
    }

    private PasswordCodeResponse responseFor(PasswordResetVerification verification) {
        User user = verification.getUser();
        return new PasswordCodeResponse(
                verification.getId(),
                verification.getChannel() == VerificationChannel.EMAIL
                        ? maskEmail(user.getEmail())
                        : maskPhone(user.getPhone()),
                codeExpiration.toSeconds()
        );
    }

    private String generateCode() {
        return String.format(Locale.ROOT, "%06d", secureRandom.nextInt(1_000_000));
    }

    private String maskEmail(String email) {
        int separator = email.indexOf('@');
        if (separator <= 1) return "***" + email.substring(Math.max(separator, 0));
        return email.charAt(0) + "***" + email.substring(separator);
    }

    private String maskPhone(String phone) {
        if (phone.length() <= 4) return "****";
        return "*".repeat(phone.length() - 4) + phone.substring(phone.length() - 4);
    }
}
