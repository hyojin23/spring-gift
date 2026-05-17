package gift.product.exception;

public class AdminProductCategoryNotFoundException extends AdminProductException {

    public AdminProductCategoryNotFoundException(Long id) {
        super("카테고리가 존재하지 않습니다. id=" + id);
    }
}
