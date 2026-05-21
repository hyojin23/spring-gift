# Quickstart: KakaoLoginClient 요청 구성 리팩토링

## 실행 전 확인

```powershell
git status --short
```

## 구현 범위

1. `KakaoLoginClient`의 카카오 API URI 상수화
2. access token 요청 form parameter 생성 method 분리
3. user info 요청 Bearer header 생성 method 분리
4. content type 표현 정리
5. `KakaoLoginClientTest`로 요청 URI/header/body 검증

## 검증 명령

```powershell
.\gradlew.bat test --tests *KakaoLoginClient*
.\gradlew.bat test --tests *KakaoAuth*
.\gradlew.bat test
```

## 기대 결과

- token 요청 body는 기존 form parameter를 모두 포함합니다.
- token 요청 content type은 form urlencoded입니다.
- user info 요청은 기존 Bearer Authorization header를 포함합니다.
- 카카오 인증 service/controller 테스트가 그대로 통과합니다.
