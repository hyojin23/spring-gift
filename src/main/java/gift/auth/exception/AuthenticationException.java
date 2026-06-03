package gift.auth.exception;

public class AuthenticationException extends RuntimeException {

    public AuthenticationException() {
        super("인증 정보가 없거나 유효하지 않습니다.");
    }
}