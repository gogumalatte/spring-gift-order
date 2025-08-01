package gift.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.client.KakaoClient;
import gift.dto.KakaoTokenResponse;
import gift.dto.KakaoUserInfoResponse;
import gift.entity.Order;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class KakaoApiService {

    private final KakaoClient kakaoClient;
    private final String clientId;
    private final String redirectUri;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public KakaoApiService(
        KakaoClient kakaoClient,
        @Value("${kakao.client.id}") String clientId,
        @Value("${kakao.redirect.uri}") String redirectUri
    ) {
        this.kakaoClient = kakaoClient;
        this.clientId = clientId;
        this.redirectUri = redirectUri;
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

    public void sendMessageToMe(String accessToken, Order order) {
        try {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

            Map<String, Object> template = Map.of(
                "object_type", "text",
                "text", String.format(
                    "주문이 완료되었습니다! 🎉\n\n- 상품명: %s\n- 옵션: %s\n- 수량: %d개\n- 메시지: %s",
                    order.getOption().getItem().getName(),
                    order.getOption().getName(),
                    order.getQuantity(),
                    order.getOrderMessage()
                ),
                "link", Map.of(
                    "web_url", "http://localhost:8080/admin/items",
                    "mobile_web_url", "http://localhost:8080/admin/items"
                ),
                "button_title", "주문 내역 확인"
            );

            String templateJson = objectMapper.writeValueAsString(template);
            body.add("template_object", templateJson);

            kakaoClient.sendKakaoTalkMessage(accessToken, body);
        } catch (Exception e) {
            throw new RuntimeException("카카오톡 메시지 전송에 실패했습니다.", e);
        }
    }
}