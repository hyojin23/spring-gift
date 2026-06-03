package gift.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import feign.FeignException;
import gift.auth.exception.KakaoLoginException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
public class KakaoLoginClient {
    private static final String AUTHORIZATION_CODE = "authorization_code";
    private static final String BEARER_PREFIX = "Bearer ";

    private final KakaoLoginProperties properties;
    private final KakaoTokenFeignClient tokenFeignClient;
    private final KakaoUserFeignClient userFeignClient;

    public KakaoLoginClient(
        KakaoLoginProperties properties,
        KakaoTokenFeignClient tokenFeignClient,
        KakaoUserFeignClient userFeignClient
    ) {
        this.properties = properties;
        this.tokenFeignClient = tokenFeignClient;
        this.userFeignClient = userFeignClient;
    }

    public KakaoTokenResponse requestAccessToken(String code) {
        KakaoTokenResponse response;
        try {
            response = tokenFeignClient.requestAccessToken(createAccessTokenRequestParams(code));
        } catch (FeignException e) {
            throw new KakaoLoginException("카카오 access token 요청에 실패했습니다.", e);
        }
        return requireResponse(response);
    }

    public KakaoUserResponse requestUserInfo(String accessToken) {
        KakaoUserResponse response;
        try {
            response = userFeignClient.requestUserInfo(bearerToken(accessToken));
        } catch (FeignException e) {
            throw new KakaoLoginException("카카오 사용자 정보 요청에 실패했습니다.", e);
        }
        return requireResponse(response);
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

    private KakaoTokenResponse requireResponse(KakaoTokenResponse response) {
        if (response == null) {
            throw new KakaoLoginException("카카오 access token 응답이 비어 있습니다.");
        }
        return response;
    }

    private KakaoUserResponse requireResponse(KakaoUserResponse response) {
        if (response == null) {
            throw new KakaoLoginException("카카오 사용자 정보 응답이 비어 있습니다.");
        }
        return response;
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
