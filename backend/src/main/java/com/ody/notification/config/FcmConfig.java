package com.ody.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@Configuration
public class FcmConfig {

    @PostConstruct
    public void initialize() {
        try {
            FirebaseApp.initializeApp(buildOptions());
            log.info("Fcm 설정 성공");
        } catch (IOException exception) {
            log.error("Fcm 연결 오류 {}", exception.getMessage());
        }
    }

    private FirebaseOptions buildOptions() throws IOException {
        return FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(new ClassPathResource("fcm-admin-sdk.json")
                        .getInputStream()))
                .build();
    }
}
