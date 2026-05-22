package gift.wish.exception;

public class WishProductNotFoundException extends WishException {

    public WishProductNotFoundException() {
        super("위시에 추가할 상품을 찾을 수 없습니다.");
    }
}
