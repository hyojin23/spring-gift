package gift.member.exception;

public class InsufficientMemberPointException extends MemberException {

    public InsufficientMemberPointException() {
        super("포인트가 부족합니다.");
    }
}
