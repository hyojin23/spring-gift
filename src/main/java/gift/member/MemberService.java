package gift.member;

import gift.member.exception.DuplicateMemberEmailException;
import gift.member.exception.InvalidMemberCredentialsException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Member register(MemberRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new DuplicateMemberEmailException();
        }

        try {
            String encodedPassword = passwordEncoder.encode(request.password());
            return memberRepository.saveAndFlush(new Member(request.email(), encodedPassword));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateMemberEmailException();
        }
    }

    public Member authenticate(MemberRequest request) {
        Member member = memberRepository.findByEmail(request.email())
            .orElseThrow(InvalidMemberCredentialsException::new);

        if (member.getPassword() == null || !passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new InvalidMemberCredentialsException();
        }

        return member;
    }

    @Transactional
    public Member deductPointForOrder(Long memberId, int amount) {
        Member member = memberRepository.findByIdForUpdate(memberId)
            .orElseThrow(InvalidMemberCredentialsException::new);
        member.deductPoint(amount);
        return member;
    }
}
