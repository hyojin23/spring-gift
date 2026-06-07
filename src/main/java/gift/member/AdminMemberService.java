package gift.member;

import gift.member.exception.AdminMemberNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminMemberService {

    private final MemberRepository memberRepository;
    private final MemberPasswordEncoder memberPasswordEncoder;

    public AdminMemberService(MemberRepository memberRepository, MemberPasswordEncoder memberPasswordEncoder) {
        this.memberRepository = memberRepository;
        this.memberPasswordEncoder = memberPasswordEncoder;
    }

    public List<Member> getMembers() {
        return memberRepository.findAll();
    }

    public Member getMember(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new AdminMemberNotFoundException(id));
    }

    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Transactional
    public void createMember(String email, String password) {
        memberRepository.save(new Member(email, memberPasswordEncoder.encode(password)));
    }

    @Transactional
    public void updateMember(Long id, String email, String password) {
        Member member = getMember(id);
        member.update(email, memberPasswordEncoder.encode(password));
        memberRepository.save(member);
    }

    @Transactional
    public void chargePoint(Long id, int amount) {
        Member member = getMember(id);
        member.chargePoint(amount);
        memberRepository.save(member);
    }

    @Transactional
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }
}
