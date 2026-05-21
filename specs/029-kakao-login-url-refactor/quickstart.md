# Quickstart: 카카오 로그인 URL 구성 분리 리팩토링

## 실행 전 확인

```powershell
git status --short
```

## 구현 범위

1. `KakaoLoginUrlProvider` 추가
2. provider에서 카카오 authorization URL 생성
3. `KakaoAuthController`가 provider를 사용하도록 변경
4. provider 단위 테스트 추가
5. controller login 테스트를 provider mock 기준으로 갱신

## 검증 명령

```powershell
.\gradlew.bat test --tests *KakaoLoginUrl*
.\gradlew.bat test --tests *KakaoAuth*
.\gradlew.bat test
```

## 기대 결과

- provider는 기존과 같은 카카오 authorization URL을 생성합니다.
- `/api/auth/kakao/login`은 provider가 반환한 URL로 redirect 합니다.
- `/api/auth/kakao/callback` 동작은 유지됩니다.
