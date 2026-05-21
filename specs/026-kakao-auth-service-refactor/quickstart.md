# Quickstart: KakaoAuthController 서비스 분리 리팩토링

## 실행 전 확인

```powershell
git status --short
```

## 구현 범위

1. `KakaoAuthService` 추가
2. callback 인증 흐름을 controller에서 service로 이동
3. controller callback은 service 호출 후 `TokenResponse` 반환
4. 신규/기존 회원 callback 흐름 service 테스트 추가
5. login redirect와 callback 응답 controller 테스트 추가

## 검증 명령

```powershell
.\gradlew.bat test --tests *KakaoAuth*
.\gradlew.bat test
```

## 기대 결과

- `/api/auth/kakao/login` redirect 동작은 유지됩니다.
- `/api/auth/kakao/callback` 응답 body는 기존처럼 token을 포함합니다.
- 신규 회원은 생성/저장됩니다.
- 기존 회원은 카카오 access token이 갱신되고 저장됩니다.
