package gift.member.exception;

public class AdminMemberNotFoundException extends MemberException {

    public AdminMemberNotFoundException(Long id) {
        super("회원이 존재하지 않습니다. id=" + id);
    }
}
