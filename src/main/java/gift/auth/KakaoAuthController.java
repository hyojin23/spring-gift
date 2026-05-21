package gift.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * Handles the Kakao OAuth2 login flow.
 * 1. /login redirects the user to Kakao's authorization page
 * 2. /callback receives the authorization code and delegates the Kakao authentication flow
 *    to KakaoAuthService.
 */
@RestController
@RequestMapping(path = "/api/auth/kakao")
public class KakaoAuthController {
    private final KakaoLoginUrlProvider kakaoLoginUrlProvider;
    private final KakaoAuthService kakaoAuthService;

    public KakaoAuthController(
        KakaoLoginUrlProvider kakaoLoginUrlProvider,
        KakaoAuthService kakaoAuthService
    ) {
        this.kakaoLoginUrlProvider = kakaoLoginUrlProvider;
        this.kakaoAuthService = kakaoAuthService;
    }

    @GetMapping(path = "/login")
    public ResponseEntity<Void> login() {
        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, kakaoLoginUrlProvider.createLoginUrl())
            .build();
    }

    @GetMapping(path = "/callback")
    public ResponseEntity<TokenResponse> callback(@RequestParam("code") String code) {
        String token = kakaoAuthService.login(code);
        return ResponseEntity.ok(new TokenResponse(token));
    }
}
