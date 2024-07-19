package com.ody.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@Configuration
public class FcmConfig {

    @Value("${fcm.enabled}")
    private boolean fcmEnabled;

    @Value("${fcm.access-key-filename}")
    private String accessKeyFilename;

    @PostConstruct
    public void initialize() {
        if (!fcmEnabled) {
            log.info("Fcm 설정이 비활성화되었습니다.");
            return;
        }

        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(
                            GoogleCredentials.fromStream(new ClassPathResource(accessKeyFilename).getInputStream())
                    )
                    .build();
            FirebaseApp.initializeApp(options);
            log.info("Fcm 설정 성공");
        } catch (IOException exception) {
            log.error("Fcm 연결 오류 {}", exception.getMessage());
        }
    }
}
