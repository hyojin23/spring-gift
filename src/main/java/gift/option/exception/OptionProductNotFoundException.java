package gift.option.exception;

public class OptionProductNotFoundException extends OptionException {

    public OptionProductNotFoundException() {
        super("요청한 상품을 찾을 수 없습니다.");
    }
}
