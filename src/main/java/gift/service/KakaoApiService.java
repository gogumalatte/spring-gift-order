package gift.service;

import gift.dto.KakaoTokenResponse;
import gift.dto.KakaoUserInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class KakaoApiService {

    private final RestClient restClient;

    @Value("${kakao.client.id}")
    private String clientId;

    @Value("${kakao.redirect.uri}")
    private String redirectUri;

    public KakaoApiService(RestClient restClient) {
        this.restClient = restClient;
    }

    public String getAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        KakaoTokenResponse response = restClient.post()
            .uri(tokenUrl)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(body)
            .retrieve()
            .body(KakaoTokenResponse.class);

        if (response == null) {
            throw new RuntimeException("카카오 토큰을 발급받는데 실패했습니다.");
        }
        return response.accessToken();
    }

    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        KakaoUserInfoResponse response = restClient.get()
            .uri(userInfoUrl)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .retrieve()
            .body(KakaoUserInfoResponse.class);
        
        if (response == null) {
            throw new RuntimeException("카카오 사용자 정보를 가져오는데 실패했습니다.");
        }
        return response;
    }
}