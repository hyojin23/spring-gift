package gift.option.exception;

public class OptionDeletionNotAllowedException extends OptionException {

    public OptionDeletionNotAllowedException() {
        super("옵션이 1개인 상품은 옵션을 삭제할 수 없습니다.");
    }
}
