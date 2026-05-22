# Quickstart: 인증 Member 추출 공통화 리팩토링

## 구현 순서

1. 중복 인증 추출 코드를 확인합니다.

```powershell
rg "private Member extractMember|authenticationResolver.extractMember" src/main/java/gift
```

2. `gift.auth.AuthenticatedMemberResolver`를 추가합니다.

```java
@Component
public class AuthenticatedMemberResolver {

    private final AuthenticationResolver authenticationResolver;

    public AuthenticatedMemberResolver(AuthenticationResolver authenticationResolver) {
        this.authenticationResolver = authenticationResolver;
    }

    public Member resolve(String authorization) {
        Member member = authenticationResolver.extractMember(authorization);
        if (member == null) {
            throw new AuthenticationException();
        }
        return member;
    }
}
```

3. resolver 단위 테스트를 추가합니다.

4. `OrderController`와 `WishController`에서 `AuthenticatedMemberResolver`를 주입받도록 변경합니다.

5. 각 controller의 private `extractMember()`를 제거합니다.

6. 테스트를 실행합니다.

```powershell
.\gradlew.bat test --tests *AuthenticatedMemberResolver*
.\gradlew.bat test --tests *OrderController*
.\gradlew.bat test --tests *WishController*
.\gradlew.bat test
```

## 확인 포인트

- 인증 실패 응답 code가 `AUTH.UNAUTHORIZED`로 유지되는지 확인합니다.
- order/wish 성공 응답이 유지되는지 확인합니다.
- `private Member extractMember`가 controller에 남아 있지 않은지 확인합니다.
