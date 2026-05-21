# Quickstart: JwtProvider 테스트 보강 리팩토링

## 구현 순서

1. `src/test/java/gift/auth/JwtProviderTest.java`를 추가합니다.
2. HS256에 충분한 길이의 테스트 secret을 정의합니다.
3. `new JwtProvider(secret, expiration)` helper를 작성합니다.
4. 생성한 token에서 email을 추출하는 테스트를 작성합니다.
5. 음수 expiration으로 만료 token 테스트를 작성합니다.
6. malformed token 테스트를 작성합니다.
7. 다른 secret으로 검증 실패 테스트를 작성합니다.
8. null/blank token 실패 테스트를 작성합니다.
9. 관련 테스트를 실행합니다.

## 검증 명령

```powershell
.\gradlew.bat test --tests *JwtProvider*
.\gradlew.bat test --tests *AuthenticationResolver*
```

## 수동 확인 포인트

- `JwtProvider` production code는 변경하지 않습니다.
- 테스트는 Spring context 없이 빠르게 실행됩니다.
- 후속 `InvalidTokenException` 도입을 위한 현재 실패 동작이 테스트로 고정됩니다.
