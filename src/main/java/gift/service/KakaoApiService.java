package gift.service;

import gift.dto.KakaoTokenResponse;
import gift.dto.KakaoUserInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoApiService {

    private final RestTemplate restTemplate;

    @Value("${kakao.client.id}")
    private String clientId;

    @Value("${kakao.client.secret}")
    private String clientSecret;

    @Value("${kakao.redirect.uri}")
    private String redirectUri;

    public KakaoApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        KakaoTokenResponse response = restTemplate.exchange(
            tokenUrl, HttpMethod.POST, requestEntity, KakaoTokenResponse.class
        ).getBody();
        
        if (response == null) {
            throw new RuntimeException("카카오 토큰을 발급받는데 실패했습니다.");
        }
        return response.accessToken();
    }

    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        KakaoUserInfoResponse response = restTemplate.exchange(
            userInfoUrl, HttpMethod.GET, requestEntity, KakaoUserInfoResponse.class
        ).getBody();
        
        if (response == null) {
            throw new RuntimeException("카카오 사용자 정보를 가져오는데 실패했습니다.");
        }
        return response;
    }
}