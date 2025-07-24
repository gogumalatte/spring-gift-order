package gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public record KakaoUserInfoResponse(
    Long id,
    @JsonProperty("kakao_account") Map<String, Object> kakaoAccount,
    @JsonProperty("properties") Map<String, String> properties
) {
    public String getEmail() {
        if (kakaoAccount != null && kakaoAccount.containsKey("email")) {
            return (String) kakaoAccount.get("email");
        }
        return null;
    }

    public String getNickname() {
        if (properties != null && properties.containsKey("nickname")) {
            return properties.get("nickname");
        }
        return null;
    }

    public String getProfileImageUrl() {
        if (properties != null && properties.containsKey("profile_image")) {
            return properties.get("profile_image");
        }
        return null;
    }
}