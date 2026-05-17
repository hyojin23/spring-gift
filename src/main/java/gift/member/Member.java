package gift.member;

import gift.member.exception.MemberValidationException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Represents a registered member.
 *
 * @author brian.kim
 * @since 1.0
 */
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String kakaoAccessToken;

    private int point;

    protected Member() {
    }

    public Member(String email, String password) {
        validateEmail(email);
        validatePassword(password);
        this.email = email;
        this.password = password;
    }

    public Member(String email) {
        validateEmail(email);
        this.email = email;
    }

    public void update(String email, String password) {
        validateEmail(email);
        validatePassword(password);
        this.email = email;
        this.password = password;
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new MemberValidationException("회원 이메일은 필수입니다.");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new MemberValidationException("회원 비밀번호는 필수입니다.");
        }
    }

    public void updateKakaoAccessToken(String kakaoAccessToken) {
        this.kakaoAccessToken = kakaoAccessToken;
    }

    public void chargePoint(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        this.point += amount;
    }

    // point deduction for order payment
    public void deductPoint(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("차감 금액은 1 이상이어야 합니다.");
        }
        if (amount > this.point) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        this.point -= amount;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getKakaoAccessToken() {
        return kakaoAccessToken;
    }

    public int getPoint() {
        return point;
    }
}
