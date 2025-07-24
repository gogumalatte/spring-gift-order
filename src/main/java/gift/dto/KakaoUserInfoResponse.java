package gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public record KakaoUserInfoResponse(
    Long id,
    @JsonProperty("kakao_account") Map<String, Object> kakaoAccount
) {
    public String getEmail() {
        if (kakaoAccount != null && kakaoAccount.containsKey("email")) {
            return (String) kakaoAccount.get("email");
        }
        return null;
    }
}