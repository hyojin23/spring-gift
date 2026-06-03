package gift.member;

import gift.member.exception.DuplicateMemberEmailException;
import gift.member.exception.InvalidMemberCredentialsException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Member register(MemberRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new DuplicateMemberEmailException();
        }

        try {
            return memberRepository.saveAndFlush(new Member(request.email(), request.password()));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateMemberEmailException();
        }
    }

    public Member authenticate(MemberRequest request) {
        Member member = memberRepository.findByEmail(request.email())
            .orElseThrow(InvalidMemberCredentialsException::new);

        if (member.getPassword() == null || !member.getPassword().equals(request.password())) {
            throw new InvalidMemberCredentialsException();
        }

        return member;
    }

    @Transactional
    public void deductPointForOrder(Member member, int amount) {
        member.deductPoint(amount);
        memberRepository.save(member);
    }
}
