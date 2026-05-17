# Implementation Plan: Member 포인트 예외 정리 리팩토링

**Branch**: `013-member-point-exception-refactor` | **Date**: 2026-05-18 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/013-member-point-exception-refactor/spec.md`

**Note**: 이 문서는 Member 포인트 충전/차감 메서드의 `IllegalArgumentException`을 member 도메인 예외로 교체하는 계획입니다.

## Summary

`Member.chargePoint()`와 `Member.deductPoint()`는 현재 금액 오류와 포인트 부족 상황에서 `IllegalArgumentException`을 직접 발생시킵니다. 이를 `InvalidMemberPointAmountException`, `InsufficientMemberPointException`으로 분리해 포인트 정책 위반을 명확히 표현합니다. 포인트 예외 메시지는 한글로 통일합니다. Admin/Order controller의 에러 처리 정책 변경은 후속 작업으로 분리합니다.

## Technical Context

**Language/Version**: Java 21, Kotlin 1.9, Spring Boot 3.5.9  
**Primary Dependencies**: Spring Boot Web MVC, Spring Data JPA  
**Storage**: Spring Data JPA 기반 MySQL/H2  
**Testing**: JUnit 5 / AssertJ  
**Target Platform**: Spring Boot 서버 애플리케이션  
**Project Type**: JSON API with HTML admin pages  
**Performance Goals**: 성능 목표 없음, 도메인 예외 명확성 우선  
**Constraints**: Admin/Order controller 처리 정책 변경 없음, 포인트 메서드 동작 유지, 포인트 예외 메시지 한글 통일  
**Scale/Scope**: `Member`, member point exception classes, `MemberTest`, member/order regression tests

## Constitution Check

- Domain-First Architecture: 포인트 정책 위반을 member 도메인 예외로 표현합니다.
- Test-Driven Stability: 충전/차감 성공과 실패를 Member 단위 테스트로 고정합니다.
- Structural and Behavioral Separation: 도메인 예외 정리와 controller 에러 처리 개선을 분리합니다.
- Consistent API and Error Handling: member 예외 계층을 따릅니다.
- Maintainable Simplicity: 금액 오류와 포인트 부족만 분리합니다.
- Small Scoped Changes: AdminMemberController와 OrderController의 구조 변경은 포함하지 않습니다.

> GATE: 현재 계획은 Constitution 위반이 없으며, 주요 제약은 controller 처리 정책을 이번 작업에서 넓히지 않는 것입니다.

## Project Structure

### Documentation (this feature)

```text
specs/013-member-point-exception-refactor/
├── data-model.md
├── plan.md
├── quickstart.md
├── research.md
├── spec.md
└── tasks.md
```

### Source Code (repository root)

```text
src/main/java/gift/member/
├── Member.java
└── exception/
    ├── InsufficientMemberPointException.java
    ├── InvalidMemberPointAmountException.java
    └── MemberException.java
```

```text
src/test/java/gift/member/
└── MemberTest.java
```

```text
src/main/java/gift/order/
└── OrderController.java
```

**Structure Decision**: 포인트 예외는 기존 `gift.member.exception` 패키지에 두고 `MemberException`을 상속합니다.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | 해당 없음 | 해당 없음 |

## Phase 0: Outline & Research

1. `chargePoint()`와 `deductPoint()`의 예외 발생 지점을 확인합니다.
2. 포인트 메서드 호출자인 AdminMemberController와 OrderController를 확인합니다.
3. 기존 MemberTest 구조를 확인합니다.

## Phase 1: Design & Contracts

1. `InvalidMemberPointAmountException`을 추가합니다.
2. `InsufficientMemberPointException`을 추가합니다.
3. 두 예외의 메시지를 한글로 정의합니다.
4. `Member` 포인트 메서드에서 예외를 교체합니다.
5. 포인트 성공/실패 단위 테스트를 추가합니다.
6. Member/Order 관련 테스트를 실행합니다.

## Phase 2: Task Planning Approach

1. Member point test 추가
2. point exception class 추가
3. `Member` exception 교체
4. scope regression 확인
5. tasks 완료 표시

## Risk Assessment

- **Controller 예외 처리 미정**: 기존에도 controller에서 명시 처리하지 않았으므로 이번 작업은 도메인 예외 타입 정리에 집중합니다.
- **Order flow 영향**: `deductPoint()` 호출 지점은 유지하고 예외 타입만 변경합니다.
- **부분 변경 위험**: 포인트 검증은 point 변경 전에 수행합니다.
