package gift.auth;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtProviderTest {

    private static final String SECRET = "test-secret-key-for-jwt-provider-test-1234567890";
    private static final String OTHER_SECRET = "other-secret-key-for-jwt-provider-test-123456789";
    private static final long ONE_HOUR = 60 * 60 * 1000L;

    @Test
    @DisplayName("생성한 토큰에서 이메일을 추출한다")
    void getEmail() {
        JwtProvider jwtProvider = jwtProvider(ONE_HOUR);

        String token = jwtProvider.createToken("member@example.com");

        assertThat(jwtProvider.getEmail(token)).isEqualTo("member@example.com");
    }

    @Test
    @DisplayName("만료된 토큰은 파싱에 실패한다")
    void getEmailWithExpiredToken() {
        JwtProvider jwtProvider = jwtProvider(-1000L);
        String token = jwtProvider.createToken("member@example.com");

        assertThatThrownBy(() -> jwtProvider.getEmail(token))
            .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("잘못된 형식의 토큰은 파싱에 실패한다")
    void getEmailWithMalformedToken() {
        JwtProvider jwtProvider = jwtProvider(ONE_HOUR);

        assertThatThrownBy(() -> jwtProvider.getEmail("invalid-token"))
            .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("다른 secret으로 생성한 토큰은 검증에 실패한다")
    void getEmailWithDifferentSecret() {
        JwtProvider jwtProvider = jwtProvider(ONE_HOUR);
        JwtProvider otherJwtProvider = new JwtProvider(OTHER_SECRET, ONE_HOUR);
        String token = jwtProvider.createToken("member@example.com");

        assertThatThrownBy(() -> otherJwtProvider.getEmail(token))
            .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("null 토큰은 파싱에 실패한다")
    void getEmailWithNullToken() {
        JwtProvider jwtProvider = jwtProvider(ONE_HOUR);

        assertThatThrownBy(() -> jwtProvider.getEmail(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("blank 토큰은 파싱에 실패한다")
    void getEmailWithBlankToken() {
        JwtProvider jwtProvider = jwtProvider(ONE_HOUR);

        assertThatThrownBy(() -> jwtProvider.getEmail("   "))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private JwtProvider jwtProvider(long expiration) {
        return new JwtProvider(SECRET, expiration);
    }
}
