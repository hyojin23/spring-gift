package gift.product.exception;

public class AdminProductDeletionNotAllowedException extends AdminProductException {

    public AdminProductDeletionNotAllowedException(Long id) {
        super("옵션이 있는 상품은 삭제할 수 없습니다. id=" + id);
    }
}
