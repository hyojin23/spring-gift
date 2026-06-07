package gift.member.exception;

public class AdminMemberDeletionNotAllowedException extends MemberException {

    public AdminMemberDeletionNotAllowedException(Long id) {
        super("주문 이력이 있는 회원은 삭제할 수 없습니다. id=" + id);
    }
}
