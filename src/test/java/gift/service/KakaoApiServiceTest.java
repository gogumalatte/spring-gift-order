package gift.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import gift.dto.KakaoTokenResponse;
import gift.dto.KakaoUserInfoResponse;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class KakaoApiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private KakaoApiService kakaoApiService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kakaoApiService, "clientId", "test-client-id");
        ReflectionTestUtils.setField(kakaoApiService, "redirectUri", "http://localhost/callback");
    }

    @Test
    @DisplayName("액세스 토큰 받기 테스트")
    void getAccessToken() {
        KakaoTokenResponse mockResponse = new KakaoTokenResponse("bearer", "test-access-token", null, null, null, null);

        when(restTemplate.exchange(
            eq("https://kauth.kakao.com/oauth/token"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(KakaoTokenResponse.class)
        )).thenReturn(ResponseEntity.ok(mockResponse));

        String accessToken = kakaoApiService.getAccessToken("test-auth-code");

        assertThat(accessToken).isEqualTo("test-access-token");
    }

    @Test
    @DisplayName("사용자 정보 받기 테스트")
    void getUserInfo() {
        KakaoUserInfoResponse mockResponse = new KakaoUserInfoResponse(1L,
            Map.of("email", "test@example.com"),
            Map.of("nickname", "테스트유저"));

        when(restTemplate.exchange(
            eq("https://kapi.kakao.com/v2/user/me"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(KakaoUserInfoResponse.class)
        )).thenReturn(ResponseEntity.ok(mockResponse));

        KakaoUserInfoResponse userInfo = kakaoApiService.getUserInfo("test-access-token");

        assertThat(userInfo.getEmail()).isEqualTo("test@example.com");

        assertThat(userInfo.getNickname()).isEqualTo("테스트유저");
    }
}