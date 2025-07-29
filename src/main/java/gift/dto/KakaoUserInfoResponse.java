package gift.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserInfoResponse(
    Long id,
    KakaoAccount kakaoAccount,
    Properties properties
) {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Properties(
        String nickname,
        String profileImageUrl,
        String thumbnailImageUrl
    ) {
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record KakaoAccount(
        boolean profileNeedsAgreement,
        boolean profileNicknameNeedsAgreement,
        boolean profileImageNeedsAgreement,
        Profile profile,
        boolean emailNeedsAgreement,
        boolean isEmailValid,
        boolean isEmailVerified,
        String email
    ) {

        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public record Profile(
            String nickname,
            String thumbnailImageUrl,
            String profileImageUrl,
            boolean isDefaultImage
        ) {
        }
    }

    public String getEmail() {
        return kakaoAccount != null ? kakaoAccount.email() : null;
    }

    public String getNickname() {
        if (kakaoAccount != null && kakaoAccount.profile() != null && kakaoAccount.profile().nickname() != null) {
            return kakaoAccount.profile().nickname();
        }
        return properties != null ? properties.nickname() : null;
    }

    public String getProfileImageUrl() {
        if (kakaoAccount != null && kakaoAccount.profile() != null && kakaoAccount.profile().profileImageUrl() != null) {
            return kakaoAccount.profile().profileImageUrl();
        }
        if (properties != null && properties.profileImageUrl() != null) {
            return properties.profileImageUrl();
        }
        return null;
    }
}