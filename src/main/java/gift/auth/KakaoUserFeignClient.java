package gift.auth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    name = "kakaoUserFeignClient",
    url = "${kakao.login.user-info-base-uri}",
    path = "${kakao.login.user-info-path}"
)
public interface KakaoUserFeignClient {

    @GetMapping
    KakaoLoginClient.KakaoUserResponse requestUserInfo(
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    );
}
