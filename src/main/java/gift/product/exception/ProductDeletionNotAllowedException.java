package gift.product.exception;

public class ProductDeletionNotAllowedException extends ProductException {

    public ProductDeletionNotAllowedException() {
        super("옵션이 있는 상품은 삭제할 수 없습니다.");
    }
}
