package gift.auth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "kakaoTokenFeignClient",
    url = "${kakao.login.token-base-uri}",
    path = "${kakao.login.token-path}"
)
public interface KakaoTokenFeignClient {

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    KakaoLoginClient.KakaoTokenResponse requestAccessToken(@RequestBody MultiValueMap<String, String> params);
}
