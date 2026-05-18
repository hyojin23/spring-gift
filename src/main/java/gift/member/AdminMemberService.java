package gift.member;

import gift.member.exception.AdminMemberNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminMemberService {

    private final MemberRepository memberRepository;

    public AdminMemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
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

    public void createMember(String email, String password) {
        memberRepository.save(new Member(email, password));
    }

    public void updateMember(Long id, String email, String password) {
        Member member = getMember(id);
        member.update(email, password);
        memberRepository.save(member);
    }

    public void chargePoint(Long id, int amount) {
        Member member = getMember(id);
        member.chargePoint(amount);
        memberRepository.save(member);
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }
}
