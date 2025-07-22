package gift.util;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String testSecretKey = "Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=";
    private final long testValidityInSeconds = 3600;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(testSecretKey, testValidityInSeconds);
    }

    @Test
    @DisplayName("토큰 생성 및 정보 추출 테스트")
    void createAndParseToken() {
        String testEmail = "test@example.com";
        String testRole = "USER";

        String token = jwtUtil.createToken(testEmail, testRole);

        assertThat(token).isNotNull().isNotEmpty();

        Claims claims = jwtUtil.getClaims(token);

        assertThat(claims.getSubject()).isEqualTo(testEmail);
        assertThat(claims.get("role", String.class)).isEqualTo(testRole);
    }
}