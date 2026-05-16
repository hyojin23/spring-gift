package gift.option.exception;

public class OptionNotFoundException extends OptionException {

    public OptionNotFoundException() {
        super("요청한 옵션을 찾을 수 없습니다.");
    }
}
