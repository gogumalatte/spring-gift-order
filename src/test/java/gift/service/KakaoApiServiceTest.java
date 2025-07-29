package gift.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

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
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodySpec;
import org.springframework.web.client.RestClient.RequestBodyUriSpec;
import org.springframework.web.client.RestClient.RequestHeadersSpec;
import org.springframework.web.client.RestClient.RequestHeadersUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;

@ExtendWith(MockitoExtension.class)
class KakaoApiServiceTest {

    @Mock
    private RestClient restClient;

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
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        String authCode = "test-auth-code";

        RequestBodyUriSpec requestBodyUriSpec = mock(RequestBodyUriSpec.class);
        RequestBodySpec requestBodySpec = mock(RequestBodySpec.class);
        ResponseSpec responseSpec = mock(ResponseSpec.class);

        doReturn(requestBodyUriSpec).when(restClient).post();
        doReturn(requestBodySpec).when(requestBodyUriSpec).uri(tokenUrl);
        doReturn(requestBodySpec).when(requestBodySpec).contentType(MediaType.APPLICATION_FORM_URLENCODED);
        doReturn(requestBodySpec).when(requestBodySpec).body(any(MultiValueMap.class));
        doReturn(responseSpec).when(requestBodySpec).retrieve();
        doReturn(mockResponse).when(responseSpec).body(KakaoTokenResponse.class);

        String accessToken = kakaoApiService.getAccessToken(authCode);

        assertThat(accessToken).isEqualTo("test-access-token");
    }

    @Test
    @DisplayName("사용자 정보 받기 테스트")
    void getUserInfo() {
        KakaoUserInfoResponse mockResponse = new KakaoUserInfoResponse(1L,
            Map.of("email", "test@example.com"),
            Map.of("nickname", "테스트유저"));
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        String testAccessToken = "test-access-token";

        RequestHeadersUriSpec requestHeadersUriSpec = mock(RequestHeadersUriSpec.class);
        RequestHeadersSpec requestHeadersSpec = mock(RequestHeadersSpec.class);
        ResponseSpec responseSpec = mock(ResponseSpec.class);

        doReturn(requestHeadersUriSpec).when(restClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(userInfoUrl);
        doReturn(requestHeadersSpec).when(requestHeadersSpec).header("Authorization", "Bearer " + testAccessToken);
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(mockResponse).when(responseSpec).body(KakaoUserInfoResponse.class);

        KakaoUserInfoResponse userInfo = kakaoApiService.getUserInfo(testAccessToken);

        assertThat(userInfo.getEmail()).isEqualTo("test@example.com");
        assertThat(userInfo.getNickname()).isEqualTo("테스트유저");
    }
}