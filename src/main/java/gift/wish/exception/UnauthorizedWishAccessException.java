package gift.wish.exception;

public class UnauthorizedWishAccessException extends WishException {

    public UnauthorizedWishAccessException() {
        super("이 위시 항목에 접근할 권한이 없습니다.");
    }
}
