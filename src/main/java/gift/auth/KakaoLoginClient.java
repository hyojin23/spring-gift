package gift.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoLoginClient {
    private static final String KAKAO_TOKEN_URI = "https://kauth.kakao.com/oauth/token";
    private static final String KAKAO_USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";
    private static final String AUTHORIZATION_CODE = "authorization_code";
    private static final String BEARER_PREFIX = "Bearer ";

    private final KakaoLoginProperties properties;
    private final RestClient restClient;

    public KakaoLoginClient(KakaoLoginProperties properties, RestClient.Builder builder) {
        this.properties = properties;
        this.restClient = builder.build();
    }

    public KakaoTokenResponse requestAccessToken(String code) {
        return restClient.post()
            .uri(KAKAO_TOKEN_URI)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(createAccessTokenRequestParams(code))
            .retrieve()
            .body(KakaoTokenResponse.class);
    }

    public KakaoUserResponse requestUserInfo(String accessToken) {
        return restClient.get()
            .uri(KAKAO_USER_INFO_URI)
            .header(HttpHeaders.AUTHORIZATION, bearerToken(accessToken))
            .retrieve()
            .body(KakaoUserResponse.class);
    }

    private MultiValueMap<String, String> createAccessTokenRequestParams(String code) {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", AUTHORIZATION_CODE);
        params.add("client_id", properties.clientId());
        params.add("redirect_uri", properties.redirectUri());
        params.add("code", code);
        params.add("client_secret", properties.clientSecret());
        return params;
    }

    private String bearerToken(String accessToken) {
        return BEARER_PREFIX + accessToken;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record KakaoTokenResponse(@JsonProperty("access_token") String accessToken) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record KakaoUserResponse(@JsonProperty("kakao_account") KakaoAccount kakaoAccount) {

        public String email() {
            return kakaoAccount.email();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record KakaoAccount(String email) {
        }
    }
}
