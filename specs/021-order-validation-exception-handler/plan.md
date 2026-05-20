# Implementation Plan: Order 검증 예외 전역 처리 리팩토링

**Branch**: `021-order-validation-exception-handler` | **Date**: 2026-05-20 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/021-order-validation-exception-handler/spec.md`

**Note**: 이 문서는 `OrderValidationException`을 `GlobalExceptionHandler`에서 표준 에러 응답으로 처리하기 위한 계획입니다.

## Summary

`OrderValidationException`은 주문 도메인 검증 실패를 표현하지만 현재 `GlobalExceptionHandler`에 별도 매핑이 없습니다. 이 예외를 400 Bad Request와 `ORDER.INVALID` code로 변환하는 handler를 추가하고, 기존 `OrderOptionNotFoundException` 및 다른 도메인 예외 응답은 유지합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Boot Web MVC  
**Storage**: 해당 없음  
**Testing**: JUnit 5 / AssertJ  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: JSON API  
**Performance Goals**: 성능 목표 없음, 에러 응답 일관성 개선 우선  
**Constraints**: 기존 order/member/option/product/wish error code 유지  
**Scale/Scope**: `GlobalExceptionHandler`, `GlobalExceptionHandlerTest`

## Constitution Check

- Domain-First Architecture: 주문 도메인 검증 실패를 order error code로 표현합니다.
- Test-Driven Stability: handler 테스트로 status/code/message를 고정합니다.
- Structural and Behavioral Separation: 도메인 검증 로직은 변경하지 않고 전역 응답 변환만 추가합니다.
- Consistent API and Error Handling: order 도메인 예외도 다른 도메인처럼 `ErrorResponse`로 통일합니다.
- Maintainable Simplicity: `ORDER.INVALID` 단일 code로 검증 실패를 표현합니다.
- Small Scoped Changes: global handler와 테스트에 한정합니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 기존 예외 응답 계약을 유지합니다.

## Project Structure

### Documentation (this feature)

```text
specs/021-order-validation-exception-handler/
├── data-model.md
├── plan.md
├── quickstart.md
├── research.md
├── spec.md
└── tasks.md
```

### Source Code (repository root)

```text
src/main/java/gift/global/
└── GlobalExceptionHandler.java
```

```text
src/test/java/gift/global/
└── GlobalExceptionHandlerTest.java
```

**Structure Decision**: 새 exception class는 만들지 않습니다. 기존 `OrderValidationException`을 전역 핸들러에 등록합니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. `OrderValidationException`이 존재하는지 확인합니다.
2. `GlobalExceptionHandler`에 order validation handler가 없는지 확인합니다.
3. 기존 order option not found handler code/status를 확인합니다.
4. 기존 `GlobalExceptionHandlerTest` 패턴을 확인합니다.

## Phase 1: Design & Contracts

1. `handleOrderValidation(OrderValidationException exception)` 메서드를 추가합니다.
2. status는 `HttpStatus.BAD_REQUEST`로 설정합니다.
3. code는 `ORDER.INVALID`로 설정합니다.
4. message는 `exception.getMessage()`를 사용합니다.
5. handler 테스트를 추가합니다.

## Phase 2: Task Planning Approach

1. GlobalExceptionHandlerTest에 order validation 테스트 추가
2. GlobalExceptionHandler import 추가
3. handler method 추가
4. 기존 global/order 테스트 실행
5. 검색으로 매핑 확인

## Risk Assessment

- **기존 order 404 매핑 회귀 위험**: `OrderOptionNotFoundException` handler를 유지하고 테스트로 확인합니다.
- **code naming 불일치 위험**: 기존 패턴에 맞춰 `ORDER.INVALID`를 사용합니다.
- **과도한 세분화 위험**: 검증 실패 종류별 code는 만들지 않고 단일 code로 시작합니다.
