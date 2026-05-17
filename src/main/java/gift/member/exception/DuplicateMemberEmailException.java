package gift.member.exception;

public class DuplicateMemberEmailException extends MemberException {

    public DuplicateMemberEmailException() {
        super("이미 등록된 이메일입니다.");
    }
}
