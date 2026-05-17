package gift.member;

import gift.auth.JwtProvider;
import gift.auth.TokenResponse;
import gift.member.exception.DuplicateMemberEmailException;
import gift.member.exception.InvalidMemberCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public MemberService(MemberRepository memberRepository, JwtProvider jwtProvider) {
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
    }

    public TokenResponse register(MemberRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new DuplicateMemberEmailException();
        }

        Member member = memberRepository.save(new Member(request.email(), request.password()));
        return createTokenResponse(member);
    }

    public TokenResponse login(MemberRequest request) {
        Member member = memberRepository.findByEmail(request.email())
            .orElseThrow(InvalidMemberCredentialsException::new);

        if (member.getPassword() == null || !member.getPassword().equals(request.password())) {
            throw new InvalidMemberCredentialsException();
        }

        return createTokenResponse(member);
    }

    private TokenResponse createTokenResponse(Member member) {
        return new TokenResponse(jwtProvider.createToken(member.getEmail()));
    }
}
