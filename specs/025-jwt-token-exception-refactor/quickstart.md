# Quickstart: JWT 토큰 예외 리팩토링

## 실행 전 확인

```powershell
git status --short
```

## 구현 범위

1. `src/main/java/gift/auth/exception/JwtTokenException.java` 추가
2. `JwtProvider.getEmail()`에서 token 입력과 JJWT 파싱 실패를 `JwtTokenException`으로 변환
3. `AuthenticationResolver`가 `JwtTokenException`을 인증 실패로 처리하도록 갱신
4. `JwtProviderTest` 실패 기대 예외를 `JwtTokenException`으로 변경
5. `AuthenticationResolverTest`에 새 예외 처리 케이스 반영

## 검증 명령

```powershell
.\gradlew.bat test --tests *JwtProvider*
.\gradlew.bat test --tests *AuthenticationResolver*
.\gradlew.bat test
```

## 기대 결과

- 유효 token은 email을 정상 반환합니다.
- invalid/expired/wrong-secret/null/blank token은 `JwtTokenException`으로 실패합니다.
- resolver는 `JwtTokenException` 발생 시 기존처럼 null을 반환합니다.
