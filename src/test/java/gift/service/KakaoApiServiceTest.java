package gift.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import gift.client.KakaoClient;
import gift.dto.KakaoTokenResponse;
import gift.dto.KakaoUserInfoResponse;
import gift.dto.KakaoUserInfoResponse.KakaoAccount;
import gift.dto.KakaoUserInfoResponse.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KakaoApiServiceTest {

    @Mock
    private KakaoClient kakaoClient;

    @InjectMocks
    private KakaoApiService kakaoApiService;

    @BeforeEach
    void setUp() {
        kakaoApiService = new KakaoApiService(kakaoClient, "test-client-id", "http://localhost/callback");
    }

    @Test
    @DisplayName("액세스 토큰 받기 테스트")
    void getAccessToken() {
        var mockResponse = new KakaoTokenResponse("bearer", "test-access-token", 3600, null, null, null);
        given(kakaoClient.fetchAccessToken(anyString(), anyString(), anyString()))
            .willReturn(mockResponse);

        String accessToken = kakaoApiService.getAccessToken("test-auth-code");

        assertThat(accessToken).isEqualTo("test-access-token");
    }

    @Test
    @DisplayName("사용자 정보 받기 테스트")
    void getUserInfo() {
        Properties properties = new Properties("테스트유저", "test.jpg", "thumb.jpg");
        KakaoAccount.Profile profile = new KakaoAccount.Profile("테스트유저", "thumb.jpg", "test.jpg", false);
        KakaoAccount kakaoAccount = new KakaoAccount(false, false, false, profile, false, true, true, "test@example.com");
        var mockResponse = new KakaoUserInfoResponse(1L, kakaoAccount, properties);
        given(kakaoClient.fetchUserInfo(anyString()))
            .willReturn(mockResponse);

        KakaoUserInfoResponse userInfo = kakaoApiService.getUserInfo("test-access-token");

        assertThat(userInfo.id()).isEqualTo(1L);
        assertThat(userInfo.getNickname()).isEqualTo("테스트유저");
    }
}