package gift.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Map;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserInfoResponse(
    Long id,
    Map<String, Object> kakaoAccount,
    Map<String, String> properties
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