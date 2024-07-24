package com.ody.meeting.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class InviteCodeGenerator {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random RANDOM = new Random();
    private static final int RANDOM_LENGTH = 2;

    public String encode(Long meetingId) {
        byte[] bytes = String.format("%04d", meetingId)
                .getBytes(StandardCharsets.UTF_8);
        String base64Encoded = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
        return generateRandomString(base64Encoded);
    }

    private String generateRandomString(String base64Encoded) {
        StringBuilder randomString = new StringBuilder(base64Encoded);
        for (int i = 0; i < RANDOM_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            randomString.append(CHARACTERS.charAt(index));
        }
        return randomString.toString();
    }

    public Long decode(String inviteCode) {
        String base64Part = inviteCode.substring(0, inviteCode.length() - RANDOM_LENGTH);
        byte[] decodedBytes = Base64.getUrlDecoder()
                .decode(base64Part);
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
        return Long.parseLong(decodedString.trim());
    }
}
