package gift.member.exception;

public class InvalidMemberPointAmountException extends MemberException {

    public InvalidMemberPointAmountException() {
        super("포인트 금액은 1 이상이어야 합니다.");
    }
}
