package gift.option.exception;

public class OrderedOptionDeletionNotAllowedException extends OptionException {

    public OrderedOptionDeletionNotAllowedException() {
        super("주문 이력이 있는 옵션은 삭제할 수 없습니다.");
    }
}
