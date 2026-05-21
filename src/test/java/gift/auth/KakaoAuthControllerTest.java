package gift.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class KakaoAuthControllerTest {

    private final KakaoLoginUrlProvider kakaoLoginUrlProvider = mock(KakaoLoginUrlProvider.class);
    private final KakaoAuthService kakaoAuthService = mock(KakaoAuthService.class);
    private final MockMvc mockMvc = MockMvcBuilders
        .standaloneSetup(new KakaoAuthController(kakaoLoginUrlProvider, kakaoAuthService))
        .build();

    @Test
    @DisplayName("카카오 로그인 URL로 redirect한다")
    void login() throws Exception {
        when(kakaoLoginUrlProvider.createLoginUrl()).thenReturn("https://kauth.kakao.com/oauth/authorize?client_id=id");

        mockMvc.perform(get("/api/auth/kakao/login"))
            .andExpect(status().isFound())
            .andExpect(header().string(HttpHeaders.LOCATION, "https://kauth.kakao.com/oauth/authorize?client_id=id"));

        verify(kakaoLoginUrlProvider).createLoginUrl();
    }

    @Test
    @DisplayName("카카오 callback code로 JWT 토큰을 반환한다")
    void callback() throws Exception {
        when(kakaoAuthService.login("authorization-code")).thenReturn("service-jwt-token");

        mockMvc.perform(get("/api/auth/kakao/callback")
                .param("code", "authorization-code"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("service-jwt-token"));

        verify(kakaoAuthService).login("authorization-code");
    }
}
