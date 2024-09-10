package com.ody.util;

import com.ody.common.exception.OdyServerErrorException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InviteCodeGenerator {

    private static final int BYTE_CONVERT_FOR_HEX_LENGTH = 4;
    private static final String HEX_FORMAT = "%02x";

    public static String generate() {
        String uuid = UUID.randomUUID().toString();
        byte[] hashBytes = encryptBySHA256(uuid.getBytes(StandardCharsets.UTF_8));
        return convertToHex(hashBytes, BYTE_CONVERT_FOR_HEX_LENGTH).toUpperCase();
    }

    private static byte[] encryptBySHA256(byte[] input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            return messageDigest.digest(input);
        } catch (NoSuchAlgorithmException exception) {
            log.error("알고리즘 지원 불가 에러 발생 : {}", exception.getMessage());
            throw new OdyServerErrorException(exception.getMessage());
        }
    }

    private static String convertToHex(byte[] hashBytes, int length) {
        StringBuilder sb = new StringBuilder();
        for (int byteIndex = 0; byteIndex < length; byteIndex++) {
            sb.append(String.format(HEX_FORMAT, hashBytes[byteIndex]));
        }
        return sb.toString();
    }
}
