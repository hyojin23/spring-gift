package gift.auth;

import gift.auth.exception.JwtTokenException;
import gift.member.Member;
import gift.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Resolves the authenticated member from an Authorization header.
 *
 * @author brian.kim
 * @since 1.0
 */
@Component
public class AuthenticationResolver {
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Autowired
    public AuthenticationResolver(JwtProvider jwtProvider, MemberRepository memberRepository) {
        this.jwtProvider = jwtProvider;
        this.memberRepository = memberRepository;
    }

    public Member extractMember(String authorization) {
        return extractBearerToken(authorization)
            .flatMap(this::findMemberByToken)
            .orElse(null);
    }

    private Optional<String> extractBearerToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return Optional.empty();
        }
        if (!authorization.startsWith("Bearer ")) {
            return Optional.empty();
        }
        return Optional.of(authorization.substring("Bearer ".length()));
    }

    private Optional<Member> findMemberByToken(String token) {
        try {
            final String email = jwtProvider.getEmail(token);
            return memberRepository.findByEmail(email);
        } catch (JwtTokenException e) {
            return Optional.empty();
        }
    }
}
