package gift.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class KakaoLoginClientTest {

    private static final String TOKEN_URI = "https://kauth.kakao.com/oauth/token";
    private static final String USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";

    private final KakaoLoginProperties properties = new KakaoLoginProperties(
        "kakao-client-id",
        "kakao-client-secret",
        "http://localhost:8080/api/auth/kakao/callback"
    );
    private final RestClient.Builder builder = RestClient.builder();
    private final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
    private final KakaoLoginClient kakaoLoginClient = new KakaoLoginClient(properties, builder);

    @Test
    @DisplayName("카카오 access token 요청에 form 파라미터를 포함한다")
    void requestAccessToken() {
        server.expect(once(), requestTo(TOKEN_URI))
            .andExpect(method(HttpMethod.POST))
            .andExpect(header(HttpHeaders.CONTENT_TYPE, containsString(MediaType.APPLICATION_FORM_URLENCODED_VALUE)))
            .andExpect(content().string(allOf(
                containsString("grant_type=authorization_code"),
                containsString("client_id=kakao-client-id"),
                containsString("redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fapi%2Fauth%2Fkakao%2Fcallback"),
                containsString("code=authorization-code"),
                containsString("client_secret=kakao-client-secret")
            )))
            .andRespond(withSuccess("{\"access_token\":\"kakao-access-token\"}", MediaType.APPLICATION_JSON));

        KakaoLoginClient.KakaoTokenResponse response = kakaoLoginClient.requestAccessToken("authorization-code");

        assertThat(response.accessToken()).isEqualTo("kakao-access-token");
        server.verify();
    }

    @Test
    @DisplayName("카카오 사용자 정보 요청에 Bearer Authorization header를 포함한다")
    void requestUserInfo() {
        server.expect(once(), requestTo(USER_INFO_URI))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer kakao-access-token"))
            .andRespond(withSuccess(
                "{\"kakao_account\":{\"email\":\"member@example.com\"}}",
                MediaType.APPLICATION_JSON
            ));

        KakaoLoginClient.KakaoUserResponse response = kakaoLoginClient.requestUserInfo("kakao-access-token");

        assertThat(response.email()).isEqualTo("member@example.com");
        server.verify();
    }
}
