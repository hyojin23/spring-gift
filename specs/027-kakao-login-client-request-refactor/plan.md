# Implementation Plan: KakaoLoginClient 요청 구성 리팩토링

**Branch**: `027-kakao-login-client-request-refactor` | **Date**: 2026-05-22 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/027-kakao-login-client-request-refactor/spec.md`

**Note**: 이 문서는 `KakaoLoginClient`의 카카오 API 요청 구성 로직을 작은 method와 상수로 분리하기 위한 계획입니다.

## Summary

`KakaoLoginClient`의 `requestAccessToken()` 안에 직접 작성된 form parameter 구성과 `requestUserInfo()` 안의 Bearer header 문자열 조합을 분리합니다. API URI와 content type 표현도 명확히 하되 public API와 응답 record는 유지합니다. 가능하면 `MockRestServiceServer`로 실제 RestClient 요청 구성까지 검증합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Web RestClient, Spring Test  
**Storage**: 해당 없음  
**Testing**: JUnit 5 / AssertJ / MockRestServiceServer  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: JSON API 인증 컴포넌트  
**Performance Goals**: 성능 목표 없음, 요청 구성 가독성/검증성 우선  
**Constraints**: public API 변경 없음, 카카오 실패 예외 정책 변경 없음  
**Scale/Scope**: `KakaoLoginClient`, 관련 테스트

## Constitution Check

- Domain-First Architecture: 외부 카카오 API 요청 규칙을 client 내부에 명확히 둡니다.
- Test-Driven Stability: token/userinfo 요청 구성을 테스트로 고정합니다.
- Structural and Behavioral Separation: 요청 구성과 요청 전송 흐름을 분리합니다.
- Consistent API and Error Handling: public method와 실패 전파 정책은 유지합니다.
- Maintainable Simplicity: 새 클래스를 늘리지 않고 private method와 상수로 정리합니다.
- Small Scoped Changes: `KakaoLoginClient`와 테스트에 한정합니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, behavior-preserving client refactor입니다.

## Project Structure

### Documentation (this feature)

```text
specs/027-kakao-login-client-request-refactor/
├── data-model.md
├── plan.md
├── quickstart.md
├── research.md
├── spec.md
└── tasks.md
```

### Source Code (repository root)

```text
src/main/java/gift/auth/
└── KakaoLoginClient.java
```

```text
src/test/java/gift/auth/
└── KakaoLoginClientTest.java
```

**Structure Decision**: 요청 구성 method는 `KakaoLoginClient`의 private method로 둡니다. 외부에서 재사용할 요구가 없으므로 새 builder class는 만들지 않습니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. `requestAccessToken()`의 현재 파라미터와 URI를 확인합니다.
2. `requestUserInfo()`의 현재 URI와 Authorization header를 확인합니다.
3. `RestClient` 요청 구성을 테스트할 수 있는 방식을 확인합니다.

## Phase 1: Design & Contracts

1. 카카오 token URI와 user info URI를 상수로 분리합니다.
2. token 요청 form parameter 생성 method를 추가합니다.
3. Bearer authorization header 생성 method를 추가합니다.
4. `Content-Type` 표현을 `MediaType.APPLICATION_FORM_URLENCODED` 기준으로 정리합니다.
5. public method와 record 구조는 유지합니다.

## Phase 2: Task Planning Approach

1. `KakaoLoginClientTest`에서 token 요청 body/header/URI를 검증합니다.
2. `KakaoLoginClientTest`에서 user info 요청 Authorization header/URI를 검증합니다.
3. `KakaoLoginClient` 내부 method와 상수를 정리합니다.
4. `KakaoAuthServiceTest`로 service 호출 계약 유지 여부를 확인합니다.
5. 전체 테스트를 실행합니다.

## Risk Assessment

- **요청 인코딩 테스트 불안정 위험**: form body는 포함 문자열 기준으로 검증합니다.
- **Content-Type 표현 변경 위험**: Spring `MediaType`을 사용하되 실제 요청 header를 테스트합니다.
- **과도한 추상화 위험**: private method 수준으로만 분리해 변경 범위를 작게 유지합니다.
