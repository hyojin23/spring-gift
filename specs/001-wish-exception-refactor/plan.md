# Implementation Plan: Wish 패키지 예외 처리 리팩토링

**Branch**: `001-wish-exception-refactor` | **Date**: 2026-05-17 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/001-wish-exception-refactor/spec.md`

**Note**: 이 문서는 `/speckit.plan` 명령 실행 결과이며, Wish 예외 처리 리팩토링 설계를 문서화합니다.

## Summary

`Wish` 도메인의 예외 처리 구조를 `category` 패키지와 동일한 중앙 집중식 예외 처리 패턴으로 맞춥니다. 현재 `WishController`는 인증 실패와 권한 검사, 리소스 없음 상태를 controller-level에서 직접 반환하고 있어 예외 처리 흐름이 분산되어 있습니다. 이를 `gift.wish.exception` 패키지로 옮기고, `GlobalExceptionHandler`를 확장하여 `ErrorResponse`를 반환하도록 변경합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9
**Primary Dependencies**: Spring Boot Web, Spring Data JPA, Spring Validation, Jackson, JJWT
**Storage**: Spring Data JPA 기반 MySQL/H2
**Testing**: JUnit 5 / Spring Boot Test
**Target Platform**: Spring Boot 서버 애플리케이션
**Project Type**: Web service
**Performance Goals**: 오류 응답 일관성 및 유지보수성 우선, 추가 성능 목표 없음
**Constraints**: 기존 Wish API 계약 유지, controller-level 중복 예외 로직 제거, 일관된 JSON 에러 모델 적용
**Scale/Scope**: `gift.wish` 예외 처리 리팩토링 및 글로벌 예외 처리 확장

## Constitution Check

- Domain-First Architecture: 도메인 예외는 서비스/도메인 계층에서 발생하고, 컨트롤러는 요청/응답 흐름에 집중합니다.
- Test-Driven Stability: 변경 후 전체 테스트와 추가 Wish 예외 테스트를 통해 안정성을 검증합니다.
- Structural and Behavioral Separation: 리팩토링의 주 목적은 예외 처리 구조 개선이며, 공개 API 동작은 유지합니다.
- Consistent API and Error Handling: `GlobalExceptionHandler`를 통해 공통 에러 응답 형식을 보장합니다.
- Maintainable Simplicity: controller-level 상태 반환을 제거하고 예외 기반 흐름을 단순화합니다.
- Small Scoped Changes: 변경 범위는 `gift.wish`, `gift.global`, 예외 패키지 및 관련 테스트로 제한됩니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 주요 제약은 기존 Wish API의 HTTP 상태 계약을 유지하는 것입니다.

## Project Structure

### Documentation (this feature)

```text
specs/001-wish-exception-refactor/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── error-response.md
└── spec.md
```

### Source Code (repository root)

```text
src/main/java/gift/
├── auth/
│   └── AuthenticationResolver.java
├── global/
│   ├── GlobalExceptionHandler.java
│   └── exception/
│       └── ErrorResponse.java
└── wish/
    ├── WishController.java
    ├── WishService.java
    ├── WishRepository.java
    ├── Wish.java
    ├── WishRequest.java
    ├── WishResponse.java
    ├── WishAddResult.java
    ├── WishRemoveResult.java
    └── exception/
        ├── WishException.java
        ├── WishNotFoundException.java
        ├── UnauthorizedWishAccessException.java
        └── AuthenticationException.java
```

**Structure Decision**: 단일 Spring Boot 백엔드 애플리케이션 구조를 유지합니다. 이 기능은 `gift.wish` 패키지 내부 도메인 예외와 `gift.global` 글로벌 예외 처리 계층을 중심으로 진행됩니다.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 변경은 `gift.wish` 예외 처리 일관성 확보를 위한 리팩토링 | 단순 controller-level 상태 반환 유지 시 일관성 부족
