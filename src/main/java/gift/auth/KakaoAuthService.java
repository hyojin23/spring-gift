package gift.auth;

import gift.auth.exception.KakaoLoginException;
import gift.member.Member;
import gift.member.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class KakaoAuthService {
    private final KakaoLoginClient kakaoLoginClient;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public KakaoAuthService(
        KakaoLoginClient kakaoLoginClient,
        MemberRepository memberRepository,
        JwtProvider jwtProvider
    ) {
        this.kakaoLoginClient = kakaoLoginClient;
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
    }

    public String login(String code) {
        KakaoLoginClient.KakaoTokenResponse kakaoToken = kakaoLoginClient.requestAccessToken(code);
        String accessToken = requireAccessToken(kakaoToken);
        KakaoLoginClient.KakaoUserResponse kakaoUser = kakaoLoginClient.requestUserInfo(accessToken);
        String email = requireEmail(kakaoUser);

        Member member = memberRepository.findByEmail(email)
            .orElseGet(() -> new Member(email));
        member.updateKakaoAccessToken(accessToken);
        memberRepository.save(member);

        return jwtProvider.createToken(member.getEmail());
    }

    private String requireAccessToken(KakaoLoginClient.KakaoTokenResponse kakaoToken) {
        String accessToken = kakaoToken.accessToken();
        if (accessToken == null || accessToken.isBlank()) {
            throw new KakaoLoginException("카카오 access token이 비어 있습니다.");
        }
        return accessToken;
    }

    private String requireEmail(KakaoLoginClient.KakaoUserResponse kakaoUser) {
        String email = kakaoUser.email();
        if (email == null || email.isBlank()) {
            throw new KakaoLoginException("카카오 사용자 이메일이 비어 있습니다.");
        }
        return email;
    }
}
