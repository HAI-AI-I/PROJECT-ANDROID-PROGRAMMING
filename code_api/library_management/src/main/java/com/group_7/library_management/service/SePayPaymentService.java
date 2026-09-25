package com.group_7.library_management.service;

import com.group_7.library_management.dto.BorrowPaymentResponse;
import com.group_7.library_management.dto.SePayWebhookRequest;
import com.group_7.library_management.entity.BorrowRecord;
import com.group_7.library_management.entity.BorrowStatus;
import com.group_7.library_management.entity.PaymentMethod;
import com.group_7.library_management.entity.SePayTransaction;
import com.group_7.library_management.exception.BadRequestException;
import com.group_7.library_management.exception.ConflictException;
import com.group_7.library_management.exception.ResourceNotFoundException;
import com.group_7.library_management.exception.UnauthorizedException;
import com.group_7.library_management.repository.BorrowRecordRepository;
import com.group_7.library_management.repository.SePayTransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SePayPaymentService {
    private static final Pattern PAYMENT_CODE_PATTERN = Pattern.compile("UTHGROUP2[A-Z0-9]{12}");

    private final BorrowRecordRepository borrowRecordRepository;
    private final SePayTransactionRepository transactionRepository;
    private final ObjectMapper objectMapper;
    private final String bankCode;
    private final String accountNumber;
    private final String accountName;
    private final String webhookSecret;
    private final long timestampToleranceSeconds;

    public SePayPaymentService(
            BorrowRecordRepository borrowRecordRepository,
            SePayTransactionRepository transactionRepository,
            ObjectMapper objectMapper,
            @Value("${app.sepay.bank-code:}") String bankCode,
            @Value("${app.sepay.account-number:}") String accountNumber,
            @Value("${app.sepay.account-name:}") String accountName,
            @Value("${app.sepay.webhook-secret:}") String webhookSecret,
            @Value("${app.sepay.timestamp-tolerance-seconds:300}") long timestampToleranceSeconds
    ) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.transactionRepository = transactionRepository;
        this.objectMapper = objectMapper;
        this.bankCode = bankCode.strip();
        this.accountNumber = accountNumber.strip();
        this.accountName = accountName.strip();
        this.webhookSecret = webhookSecret;
        this.timestampToleranceSeconds = timestampToleranceSeconds;
    }

    @Transactional
    public BorrowPaymentResponse getPayment(Long userId, Long orderId) {
        ensurePaymentAccountConfigured();
        BorrowRecord order = borrowRecordRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn mượn"));
        if (order.getStatus() == BorrowStatus.CANCELLED) {
            throw new ConflictException("Đơn mượn đã bị hủy");
        }
        if (order.getPaymentCode() == null || order.getPaymentCode().isBlank()) {
            order.setPaymentCode(paymentCodeFor(order));
            borrowRecordRepository.saveAndFlush(order);
        }
        String qrUrl = UriComponentsBuilder
                .fromUriString("https://vietqr.app/img")
                .queryParam("acc", accountNumber)
                .queryParam("bank", bankCode)
                .queryParam("amount", order.getTotalAmount())
                .queryParam("des", order.getPaymentCode())
                .queryParam("template", "compact")
                .queryParam("showinfo", "true")
                .queryParam("holder", accountName)
                .queryParam("store", "UTH Library")
                .build()
                .encode()
                .toUriString();
        return new BorrowPaymentResponse(
                order.getId(),
                order.getReferenceCode(),
                order.getPaymentCode(),
                order.getTotalAmount(),
                order.getPaidAmount(),
                order.getPaidAmount() >= order.getTotalAmount() ? "PAID" : "UNPAID",
                bankCode,
                accountNumber,
                accountName,
                qrUrl,
                order.getPaidAt()
        );
    }

    @Transactional
    public void processWebhook(byte[] rawBody, String signature, String timestampHeader) {
        verifySignature(rawBody, signature, timestampHeader);
        ensurePaymentAccountConfigured();
        SePayWebhookRequest request = parse(rawBody);
        validateRequiredFields(request);

        if (transactionRepository.existsBySePayTransactionId(request.id())) {
            return;
        }
        if (!"in".equalsIgnoreCase(request.transferType())) {
            throw new BadRequestException("Webhook không phải giao dịch tiền vào");
        }
        if (!accountNumber.equals(request.accountNumber().strip())) {
            throw new BadRequestException("Tài khoản nhận tiền không khớp cấu hình");
        }

        String paymentCode = extractPaymentCode(request);
        BorrowRecord order = borrowRecordRepository.findByPaymentCodeForUpdate(paymentCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy đơn mượn tương ứng với mã thanh toán"
                ));
        if (order.getStatus() == BorrowStatus.CANCELLED) {
            throw new ConflictException("Không thể thanh toán đơn đã hủy");
        }
        if (order.getPaidAmount() >= order.getTotalAmount()) {
            throw new ConflictException("Đơn mượn đã được thanh toán");
        }
        if (request.transferAmount() != order.getTotalAmount()) {
            throw new BadRequestException("Số tiền chuyển khoản không khớp tổng tiền đơn mượn");
        }

        order.markPaid(request.transferAmount(), Instant.now(), PaymentMethod.SEPAY);
        if (order.getStatus() == BorrowStatus.PENDING_PAYMENT) {
            order.setStatus(BorrowStatus.REQUESTED);
        }
        transactionRepository.save(new SePayTransaction(
                request.id(),
                order,
                safe(request.gateway()),
                request.accountNumber(),
                request.transferAmount(),
                request.transactionDate(),
                request.referenceCode(),
                new String(rawBody, StandardCharsets.UTF_8)
        ));
        borrowRecordRepository.save(order);
    }

    private void verifySignature(byte[] rawBody, String signature, String timestampHeader) {
        if (webhookSecret.isBlank()) {
            throw new UnauthorizedException("Máy chủ chưa cấu hình SePay webhook secret");
        }
        long timestamp;
        try {
            timestamp = Long.parseLong(timestampHeader);
        } catch (RuntimeException exception) {
            throw new UnauthorizedException("SePay timestamp không hợp lệ");
        }
        if (Math.abs(Instant.now().getEpochSecond() - timestamp) > timestampToleranceSeconds) {
            throw new UnauthorizedException("SePay webhook đã quá thời hạn xác thực");
        }
        String expected = "sha256=" + hmacHex(timestamp, rawBody);
        if (signature == null || !MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                signature.getBytes(StandardCharsets.UTF_8)
        )) {
            throw new UnauthorizedException("Chữ ký SePay webhook không hợp lệ");
        }
    }

    private String hmacHex(long timestamp, byte[] rawBody) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            mac.update(Long.toString(timestamp).getBytes(StandardCharsets.UTF_8));
            mac.update((byte) '.');
            return HexFormat.of().formatHex(mac.doFinal(rawBody));
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Không thể xác thực chữ ký SePay", exception);
        }
    }

    private SePayWebhookRequest parse(byte[] rawBody) {
        try {
            return objectMapper.readValue(rawBody, SePayWebhookRequest.class);
        } catch (JacksonException exception) {
            throw new BadRequestException("Dữ liệu SePay webhook không hợp lệ");
        }
    }

    private void validateRequiredFields(SePayWebhookRequest request) {
        if (request.id() == null
                || request.transferAmount() == null
                || request.transferAmount() <= 0
                || request.accountNumber() == null
                || request.transferType() == null) {
            throw new BadRequestException("SePay webhook thiếu thông tin giao dịch bắt buộc");
        }
    }

    private String extractPaymentCode(SePayWebhookRequest request) {
        String code = normalize(request.code());
        if (PAYMENT_CODE_PATTERN.matcher(code).matches()) {
            return code;
        }
        String content = request.content() == null ? "" : request.content().toUpperCase(Locale.ROOT);
        Matcher matcher = PAYMENT_CODE_PATTERN.matcher(content.replaceAll("[^A-Z0-9]", ""));
        if (matcher.find()) {
            return matcher.group();
        }
        throw new BadRequestException("Không nhận diện được mã thanh toán");
    }

    private String paymentCodeFor(BorrowRecord order) {
        return "UTH" + order.getReferenceCode()
                .replaceAll("[^A-Za-z0-9]", "")
                .toUpperCase(Locale.ROOT);
    }

    private String normalize(String value) {
        return value == null
                ? ""
                : value.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "UNKNOWN" : value.strip();
    }

    private void ensurePaymentAccountConfigured() {
        if (bankCode.isBlank() || accountNumber.isBlank()) {
            throw new ConflictException("Máy chủ chưa cấu hình ngân hàng nhận thanh toán SePay");
        }
    }
}
