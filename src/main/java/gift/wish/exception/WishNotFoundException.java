package gift.wish.exception;

public class WishNotFoundException extends WishException {

    public WishNotFoundException() {
        super("요청한 위시 항목을 찾을 수 없습니다.");
    }
}
