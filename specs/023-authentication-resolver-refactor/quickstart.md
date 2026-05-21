# Quickstart: AuthenticationResolver 토큰 파싱 리팩토링

## 구현 순서

1. `AuthenticationResolverTest`를 추가합니다.
2. Bearer token이 유효하고 회원이 있으면 회원을 반환하는 테스트를 작성합니다.
3. null/blank/non-Bearer header는 null을 반환하고 JWT를 파싱하지 않는 테스트를 작성합니다.
4. JWT 파싱 실패 시 null을 반환하고 repository를 조회하지 않는 테스트를 작성합니다.
5. 회원이 없으면 null을 반환하는 테스트를 작성합니다.
6. `AuthenticationResolver`에 `extractBearerToken()` private method를 추가합니다.
7. `AuthenticationResolver`에 `findMemberByToken()` private method를 추가합니다.
8. broad `catch (Exception)`을 제거하고 JWT 파싱 실패 범위만 처리합니다.
9. 관련 테스트를 실행합니다.

## 검증 명령

```powershell
.\gradlew.bat test --tests *AuthenticationResolver*
.\gradlew.bat test --tests *Order*
.\gradlew.bat test --tests *Wish*
```

## 수동 확인 포인트

- `replace("Bearer ", "")`가 남지 않습니다.
- `catch (Exception)`이 남지 않습니다.
- 기존 `extractMember()` signature와 null 반환 계약이 유지됩니다.
