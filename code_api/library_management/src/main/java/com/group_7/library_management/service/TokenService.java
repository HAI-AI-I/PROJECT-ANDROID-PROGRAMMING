package com.group_7.library_management.service;

import com.group_7.library_management.entity.AuthToken;
import com.group_7.library_management.entity.BiometricToken;
import com.group_7.library_management.entity.User;
import com.group_7.library_management.repository.AuthTokenRepository;
import com.group_7.library_management.repository.BiometricTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class TokenService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AuthTokenRepository authTokenRepository;
    private final BiometricTokenRepository biometricTokenRepository;

    public TokenService(
            AuthTokenRepository authTokenRepository,
            BiometricTokenRepository biometricTokenRepository
    ) {
        this.authTokenRepository = authTokenRepository;
        this.biometricTokenRepository = biometricTokenRepository;
    }

    @Transactional
    public IssuedToken issueToken(User user) {
        byte[] randomBytes = new byte[32];
        SECURE_RANDOM.nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        authTokenRepository.save(new AuthToken(hash(rawToken), user));

        return new IssuedToken(rawToken);
    }

    @Transactional(readOnly = true)
    public Optional<User> findActiveUser(String rawToken) {
        return authTokenRepository.findByTokenHash(hash(rawToken))
                .map(AuthToken::getUser)
                .filter(User::isActive);
    }

    @Transactional
    public void revokeToken(String rawToken) {
        authTokenRepository.deleteByTokenHash(hash(rawToken));
    }

    @Transactional
    public void revokeAllUserTokens(Long userId) {
        authTokenRepository.deleteAllByUserId(userId);
        biometricTokenRepository.deleteAllByUserId(userId);
    }

    @Transactional
    public String issueBiometricToken(User user) {
        String rawToken = generateToken();
        biometricTokenRepository.save(new BiometricToken(hash(rawToken), user));
        return rawToken;
    }

    @Transactional(readOnly = true)
    public Optional<User> findActiveBiometricUser(String rawToken) {
        return biometricTokenRepository.findByTokenHash(hash(rawToken))
                .map(BiometricToken::getUser)
                .filter(User::isActive);
    }

    @Transactional
    public void revokeBiometricTokens(Long userId) {
        biometricTokenRepository.deleteAllByUserId(userId);
    }

    private String generateToken() {
        byte[] randomBytes = new byte[32];
        SECURE_RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }

    public record IssuedToken(String value) {
    }
}
