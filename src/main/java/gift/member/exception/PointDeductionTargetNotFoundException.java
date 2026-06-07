package gift.member.exception;

public class PointDeductionTargetNotFoundException extends MemberException {

    public PointDeductionTargetNotFoundException() {
        super("포인트 차감 대상을 찾을 수 없습니다.");
    }
}
