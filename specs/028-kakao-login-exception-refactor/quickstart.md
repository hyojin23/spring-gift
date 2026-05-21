# Quickstart: 카카오 로그인 예외 처리 리팩토링

## 실행 전 확인

```powershell
git status --short
```

## 구현 범위

1. `gift.auth.exception.KakaoLoginException` 추가
2. `KakaoLoginClient`에서 token/user info API 호출 실패를 `KakaoLoginException`으로 변환
3. `KakaoLoginClient`에서 null response body 검증
4. `KakaoAuthService`에서 access token/email 필수 값 검증
5. 실패 케이스 테스트 추가

## 검증 명령

```powershell
.\gradlew.bat test --tests *KakaoLoginClient*
.\gradlew.bat test --tests *KakaoAuth*
.\gradlew.bat test
```

## 기대 결과

- 카카오 API 4xx/5xx 응답은 `KakaoLoginException`으로 변환됩니다.
- 카카오 응답 body 누락은 `KakaoLoginException`으로 처리됩니다.
- access token/email 누락은 `KakaoLoginException`으로 처리됩니다.
- 정상 카카오 로그인 흐름은 유지됩니다.
