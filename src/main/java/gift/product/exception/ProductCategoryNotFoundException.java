package gift.product.exception;

public class ProductCategoryNotFoundException extends ProductException {

    public ProductCategoryNotFoundException() {
        super("요청한 카테고리를 찾을 수 없습니다.");
    }
}
