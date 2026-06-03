package gift.auth;

import feign.FeignException;
import gift.auth.exception.KakaoLoginException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KakaoLoginClientTest {

    private final KakaoLoginProperties properties = new KakaoLoginProperties(
        "kakao-client-id",
        "kakao-client-secret",
        "http://localhost:8080/api/auth/kakao/callback",
        "https://kauth.kakao.com/oauth/authorize",
        "https://kauth.kakao.com",
        "/oauth/token",
        "https://kapi.kakao.com",
        "/v2/user/me"
    );
    private final KakaoTokenFeignClient tokenFeignClient = mock(KakaoTokenFeignClient.class);
    private final KakaoUserFeignClient userFeignClient = mock(KakaoUserFeignClient.class);
    private final KakaoLoginClient kakaoLoginClient = new KakaoLoginClient(
        properties,
        tokenFeignClient,
        userFeignClient
    );

    @Test
    @DisplayName("카카오 access token 요청에 form 파라미터를 포함한다")
    void requestAccessToken() {
        when(tokenFeignClient.requestAccessToken(org.mockito.ArgumentMatchers.any()))
            .thenReturn(new KakaoLoginClient.KakaoTokenResponse("kakao-access-token"));

        KakaoLoginClient.KakaoTokenResponse response = kakaoLoginClient.requestAccessToken("authorization-code");

        assertThat(response.accessToken()).isEqualTo("kakao-access-token");
        @SuppressWarnings("unchecked")
        ArgumentCaptor<MultiValueMap<String, String>> paramsCaptor = ArgumentCaptor.forClass(MultiValueMap.class);
        verify(tokenFeignClient).requestAccessToken(paramsCaptor.capture());
        MultiValueMap<String, String> params = paramsCaptor.getValue();
        assertThat(params.getFirst("grant_type")).isEqualTo("authorization_code");
        assertThat(params.getFirst("client_id")).isEqualTo("kakao-client-id");
        assertThat(params.getFirst("redirect_uri")).isEqualTo("http://localhost:8080/api/auth/kakao/callback");
        assertThat(params.getFirst("code")).isEqualTo("authorization-code");
        assertThat(params.getFirst("client_secret")).isEqualTo("kakao-client-secret");
    }

    @Test
    @DisplayName("카카오 access token 요청에 실패하면 카카오 로그인 예외가 발생한다")
    void requestAccessTokenFailure() {
        when(tokenFeignClient.requestAccessToken(org.mockito.ArgumentMatchers.any()))
            .thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> kakaoLoginClient.requestAccessToken("authorization-code"))
            .isInstanceOf(KakaoLoginException.class)
            .hasMessage("카카오 access token 요청에 실패했습니다.");
    }

    @Test
    @DisplayName("카카오 access token 응답 body가 비어 있으면 카카오 로그인 예외가 발생한다")
    void requestAccessTokenWithEmptyBody() {
        when(tokenFeignClient.requestAccessToken(org.mockito.ArgumentMatchers.any()))
            .thenReturn(null);

        assertThatThrownBy(() -> kakaoLoginClient.requestAccessToken("authorization-code"))
            .isInstanceOf(KakaoLoginException.class)
            .hasMessage("카카오 access token 응답이 비어 있습니다.");
    }

    @Test
    @DisplayName("카카오 사용자 정보 요청에 Bearer Authorization header를 포함한다")
    void requestUserInfo() {
        when(userFeignClient.requestUserInfo("Bearer kakao-access-token"))
            .thenReturn(new KakaoLoginClient.KakaoUserResponse(
                new KakaoLoginClient.KakaoUserResponse.KakaoAccount("member@example.com")
            ));

        KakaoLoginClient.KakaoUserResponse response = kakaoLoginClient.requestUserInfo("kakao-access-token");

        assertThat(response.email()).isEqualTo("member@example.com");
        verify(userFeignClient).requestUserInfo("Bearer kakao-access-token");
    }

    @Test
    @DisplayName("카카오 사용자 정보 요청에 실패하면 카카오 로그인 예외가 발생한다")
    void requestUserInfoFailure() {
        when(userFeignClient.requestUserInfo("Bearer kakao-access-token"))
            .thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> kakaoLoginClient.requestUserInfo("kakao-access-token"))
            .isInstanceOf(KakaoLoginException.class)
            .hasMessage("카카오 사용자 정보 요청에 실패했습니다.");
    }

    @Test
    @DisplayName("카카오 사용자 정보 응답 body가 비어 있으면 카카오 로그인 예외가 발생한다")
    void requestUserInfoWithEmptyBody() {
        when(userFeignClient.requestUserInfo("Bearer kakao-access-token"))
            .thenReturn(null);

        assertThatThrownBy(() -> kakaoLoginClient.requestUserInfo("kakao-access-token"))
            .isInstanceOf(KakaoLoginException.class)
            .hasMessage("카카오 사용자 정보 응답이 비어 있습니다.");
    }
}
