package com.group_7.library_management.service;

import com.group_7.library_management.dto.AuthResponse;
import com.group_7.library_management.dto.BiometricTokenResponse;
import com.group_7.library_management.dto.LoginRequest;
import com.group_7.library_management.dto.RegisterRequest;
import com.group_7.library_management.dto.UserResponse;
import com.group_7.library_management.dto.UpdateProfileRequest;
import com.group_7.library_management.entity.User;
import com.group_7.library_management.exception.ConflictException;
import com.group_7.library_management.exception.UnauthorizedException;
import com.group_7.library_management.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String fullName = normalizeFullName(request.fullName());
        String email = request.email().strip().toLowerCase(Locale.ROOT);
        String phone = request.phone().strip();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email đã được sử dụng");
        }

        if (userRepository.existsByPhone(phone)) {
            throw new ConflictException("Số điện thoại đã được sử dụng");
        }

        User user = new User(
                fullName,
                email,
                phone,
                passwordEncoder.encode(request.password())
        );

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String identifier = request.identifier().strip().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmailIgnoreCaseOrPhone(identifier, identifier)
                .orElseThrow(this::invalidCredentials);

        if (!user.isActive() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw invalidCredentials();
        }

        TokenService.IssuedToken issuedToken = tokenService.issueToken(user);
        return new AuthResponse(
                issuedToken.value(),
                "Bearer",
                UserResponse.from(user)
        );
    }

    private UnauthorizedException invalidCredentials() {
        return new UnauthorizedException("Email, số điện thoại hoặc mật khẩu không chính xác");
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .filter(User::isActive)
                .orElseThrow(() -> new UnauthorizedException("Tài khoản không còn hoạt động"));
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateCurrentUser(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .filter(User::isActive)
                .orElseThrow(() -> new UnauthorizedException("Tài khoản không còn hoạt động"));

        String fullName = normalizeFullName(request.fullName());
        String email = request.email().strip().toLowerCase(Locale.ROOT);
        String phone = request.phone().strip();

        userRepository.findByEmailIgnoreCase(email)
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> {
                    throw new ConflictException("Email đã được sử dụng");
                });
        userRepository.findByPhone(phone)
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> {
                    throw new ConflictException("Số điện thoại đã được sử dụng");
                });

        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        return UserResponse.from(userRepository.save(user));
    }

    public void logout(String rawToken) {
        tokenService.revokeToken(rawToken);
    }

    @Transactional
    public BiometricTokenResponse enableBiometric(Long userId) {
        User user = userRepository.findById(userId)
                .filter(User::isActive)
                .orElseThrow(() -> new UnauthorizedException("Tài khoản không còn hoạt động"));
        tokenService.revokeBiometricTokens(userId);
        return new BiometricTokenResponse(tokenService.issueBiometricToken(user));
    }

    @Transactional
    public AuthResponse loginWithBiometric(String credential) {
        User user = tokenService.findActiveBiometricUser(credential)
                .orElseThrow(() -> new UnauthorizedException("Đăng nhập sinh trắc học không hợp lệ"));
        TokenService.IssuedToken issuedToken = tokenService.issueToken(user);
        return new AuthResponse(issuedToken.value(), "Bearer", UserResponse.from(user));
    }

    public void disableBiometric(Long userId) {
        tokenService.revokeBiometricTokens(userId);
    }

    private String normalizeFullName(String fullName) {
        return fullName.strip().replaceAll("\\s+", " ");
    }


}
