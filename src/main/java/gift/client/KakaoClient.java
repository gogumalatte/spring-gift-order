package gift.client;

import gift.dto.KakaoTokenResponse;
import gift.dto.KakaoUserInfoResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoClient {

    private final RestClient kakaoAuthClient;
    private final RestClient kakaoApiClient;

    public KakaoClient(@Qualifier("kakaoAuthClient") RestClient kakaoAuthClient,
        @Qualifier("kakaoApiClient") RestClient kakaoApiClient) {
        this.kakaoAuthClient = kakaoAuthClient;
        this.kakaoApiClient = kakaoApiClient;
    }

    public KakaoTokenResponse fetchAccessToken(String code, String clientId, String redirectUri) {

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        return kakaoAuthClient.post()
            .uri("/oauth/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(body)
            .retrieve()
            .body(KakaoTokenResponse.class);
    }

    public KakaoUserInfoResponse fetchUserInfo(String accessToken) {
        return kakaoApiClient.get()
            .uri("/v2/user/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .retrieve()
            .body(KakaoUserInfoResponse.class);
    }

    public void sendKakaoTalkMessage(String accessToken, MultiValueMap<String, String> body) {
        kakaoApiClient.post()
            .uri("/v2/api/talk/memo/default/send")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(body)
            .retrieve()
            .toBodilessEntity();
    }
}
