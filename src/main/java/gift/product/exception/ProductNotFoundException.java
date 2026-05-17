package gift.product.exception;

public class ProductNotFoundException extends ProductException {

    public ProductNotFoundException() {
        super("요청한 상품을 찾을 수 없습니다.");
    }
}
