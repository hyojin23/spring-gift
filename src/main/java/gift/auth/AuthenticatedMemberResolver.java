package gift.auth;

import gift.member.Member;
import gift.auth.exception.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedMemberResolver {

    private final AuthenticationResolver authenticationResolver;

    public AuthenticatedMemberResolver(AuthenticationResolver authenticationResolver) {
        this.authenticationResolver = authenticationResolver;
    }

    public Member resolve(String authorization) {
        Member member = authenticationResolver.extractMember(authorization);
        if (member == null) {
            throw new AuthenticationException();
        }
        return member;
    }
}
