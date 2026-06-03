package gift.auth;

import gift.member.Member;
import gift.member.MemberRequest;
import gift.member.MemberService;
import org.springframework.stereotype.Service;

@Service
public class MemberAuthService {

    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    public MemberAuthService(MemberService memberService, JwtProvider jwtProvider) {
        this.memberService = memberService;
        this.jwtProvider = jwtProvider;
    }

    public TokenResponse register(MemberRequest request) {
        Member member = memberService.register(request);
        return createTokenResponse(member);
    }

    public TokenResponse login(MemberRequest request) {
        Member member = memberService.authenticate(request);
        return createTokenResponse(member);
    }

    private TokenResponse createTokenResponse(Member member) {
        return new TokenResponse(jwtProvider.createToken(member.getEmail()));
    }
}
