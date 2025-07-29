package gift.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {

    @Bean
    @Qualifier("kakaoAuthClient")
    public RestClient kakaoAuthClient(@Value("${kakao.api.url.auth}") String baseUrl) {
        return RestClient.builder()
            .baseUrl(baseUrl)
            .build();
    }

    @Bean
    @Qualifier("kakaoApiClient")
    public RestClient kakaoApiClient(@Value("${kakao.api.url.api}") String baseUrl) {
        return RestClient.builder()
            .baseUrl(baseUrl)
            .build();
    }
}