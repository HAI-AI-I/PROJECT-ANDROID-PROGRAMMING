package com.group_7.library_management.service;

import com.group_7.library_management.exception.SmsDeliveryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class RegistrationSmsService {

    private final RestClient restClient;
    private final String apiKey;
    private final String sender;
    private final boolean baseUrlConfigured;

    public RegistrationSmsService(
            @Value("${app.infobip.base-url:}") String baseUrl,
            @Value("${app.infobip.api-key:}") String apiKey,
            @Value("${app.infobip.sender:InfoSMS}") String sender
    ) {
        this.baseUrlConfigured = !baseUrl.isBlank();
        this.restClient = RestClient.builder()
                .baseUrl(baseUrlConfigured
                        ? normalizeBaseUrl(baseUrl)
                        : "https://invalid.infobip.local")
                .build();
        this.apiKey = apiKey;
        this.sender = sender;
    }

    public void sendRegistrationCode(String phone, String code) {
        ensureConfigured();
        InfobipSmsRequest request = new InfobipSmsRequest(List.of(
                new InfobipMessage(
                        List.of(new InfobipDestination(toInternationalDigits(phone))),
                        sender,
                        "Ma xac nhan dang ky Library Management cua ban la: " + code
                                + ". Ma co hieu luc trong 5 phut."
                )
        ));

        try {
            InfobipSmsResponse response = restClient.post()
                    .uri("/sms/2/text/advanced")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "App " + apiKey)
                    .body(request)
                    .retrieve()
                    .body(InfobipSmsResponse.class);
            if (!wasAccepted(response)) {
                throw new SmsDeliveryException("Infobip không chấp nhận yêu cầu gửi SMS");
            }
        } catch (RestClientException exception) {
            throw new SmsDeliveryException(
                    "Không thể gửi mã xác nhận tới số điện thoại này qua Infobip",
                    exception
            );
        }
    }

    private boolean wasAccepted(InfobipSmsResponse response) {
        if (response == null || response.messages() == null || response.messages().isEmpty()) {
            return false;
        }
        InfobipStatus status = response.messages().getFirst().status();
        if (status == null || status.groupName() == null) {
            return false;
        }
        String group = status.groupName().toUpperCase();
        return group.equals("PENDING") || group.equals("DELIVERED");
    }

    private void ensureConfigured() {
        if (!baseUrlConfigured || apiKey.isBlank()) {
            throw new SmsDeliveryException("Máy chủ chưa được cấu hình tài khoản Infobip");
        }
    }

    static String normalizeBaseUrl(String baseUrl) {
        String normalized = baseUrl.strip().replaceAll("/+$", "");
        if (!normalized.startsWith("https://") && !normalized.startsWith("http://")) {
            normalized = "https://" + normalized;
        }
        return normalized;
    }

    static String toInternationalDigits(String phone) {
        String normalized = phone.strip().replace("+", "");
        if (normalized.startsWith("0")) {
            return "84" + normalized.substring(1);
        }
        return normalized;
    }

    private record InfobipSmsRequest(List<InfobipMessage> messages) {
    }

    private record InfobipMessage(
            List<InfobipDestination> destinations,
            String from,
            String text
    ) {
    }

    private record InfobipDestination(String to) {
    }

    private record InfobipSmsResponse(List<InfobipResponseMessage> messages) {
    }

    private record InfobipResponseMessage(InfobipStatus status) {
    }

    private record InfobipStatus(String groupName) {
    }
}
