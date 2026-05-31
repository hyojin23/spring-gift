package gift.member.exception;

public class AdminMemberNotFoundException extends MemberException {

    public AdminMemberNotFoundException(Long id) {
        super("존재하지 않는 회원입니다.");
    }
}
