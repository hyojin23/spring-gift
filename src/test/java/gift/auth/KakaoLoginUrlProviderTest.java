package gift.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KakaoLoginUrlProviderTest {

    @Test
    @DisplayName("카카오 로그인 URL을 생성한다")
    void createLoginUrl() {
        KakaoLoginProperties properties = new KakaoLoginProperties(
            "kakao-client-id",
            "kakao-client-secret",
            "http://localhost:8080/api/auth/kakao/callback"
        );
        KakaoLoginUrlProvider provider = new KakaoLoginUrlProvider(properties);

        String loginUrl = provider.createLoginUrl();

        assertThat(loginUrl).contains("https://kauth.kakao.com/oauth/authorize");
        assertThat(loginUrl).contains("response_type=code");
        assertThat(loginUrl).contains("client_id=kakao-client-id");
        assertThat(loginUrl).contains("redirect_uri=http://localhost:8080/api/auth/kakao/callback");
        assertThat(loginUrl).contains("scope=account_email,talk_message");
    }
}
