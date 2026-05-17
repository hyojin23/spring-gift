package gift.product.exception;

public class AdminProductNotFoundException extends AdminProductException {

    public AdminProductNotFoundException(Long id) {
        super("상품이 존재하지 않습니다. id=" + id);
    }
}
