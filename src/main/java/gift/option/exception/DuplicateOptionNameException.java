package gift.option.exception;

public class DuplicateOptionNameException extends OptionException {

    public DuplicateOptionNameException() {
        super("이미 존재하는 옵션명입니다.");
    }
}
