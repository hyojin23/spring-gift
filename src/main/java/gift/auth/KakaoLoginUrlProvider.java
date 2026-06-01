package gift.auth;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class KakaoLoginUrlProvider {
    private static final String RESPONSE_TYPE_CODE = "code";
    private static final String SCOPE = "account_email,talk_message";

    private final KakaoLoginProperties properties;

    public KakaoLoginUrlProvider(KakaoLoginProperties properties) {
        this.properties = properties;
    }

    public String createLoginUrl() {
        return UriComponentsBuilder.fromUriString(properties.authorizationUri())
            .queryParam("response_type", RESPONSE_TYPE_CODE)
            .queryParam("client_id", properties.clientId())
            .queryParam("redirect_uri", properties.redirectUri())
            .queryParam("scope", SCOPE)
            .build()
            .toUriString();
    }
}
