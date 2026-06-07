package gift.category.exception;

public class CategoryDeletionNotAllowedException extends RuntimeException {

    public CategoryDeletionNotAllowedException() {
        super("상품이 있는 카테고리는 삭제할 수 없습니다.");
    }
}
