package gift.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao.login")
public record KakaoLoginProperties(
    String clientId,
    String clientSecret,
    String redirectUri,
    String authorizationUri,
    String tokenBaseUri,
    String tokenPath,
    String userInfoBaseUri,
    String userInfoPath
) {
}
