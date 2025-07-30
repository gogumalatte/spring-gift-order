package gift.service;

import gift.client.KakaoClient;
import gift.dto.KakaoTokenResponse;
import gift.dto.KakaoUserInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KakaoApiService {

    private final KakaoClient kakaoClient;
    private final String clientId;
    private final String redirectUri;

    public KakaoApiService(
        KakaoClient kakaoClient,
        @Value("${kakao.client.id}") String clientId,
        @Value("${kakao.redirect.uri}") String redirectUri
    ) {
        this.kakaoClient = kakaoClient;
        this.clientId = clientId;
        this.redirectUri = redirectUri;
    }

    public KakaoUserInfoResponse processKakaoLogin(String code) {
        String accessToken = getAccessToken(code);
        // return getUserInfo(accessToken);
        throw new UnsupportedOperationException("This method is deprecated. Use getAccessToken and getUserInfo separately.");
    }

    public String getAccessToken(String code) {
        KakaoTokenResponse response = kakaoClient.fetchAccessToken(code, clientId, redirectUri);

        if (response == null) {
            throw new RuntimeException("카카오 토큰을 발급받는데 실패했습니다.");
        }
        return response.accessToken();
    }

    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        KakaoUserInfoResponse response = kakaoClient.fetchUserInfo(accessToken);

        if (response == null) {
            throw new RuntimeException("카카오 사용자 정보를 가져오는데 실패했습니다.");
        }
        return response;
    }
}