# Research: Category 예외 패키지 정리 리팩토링

## 결정 1: category 예외를 `gift.category.exception` 패키지로 이동

**결정**: `CategoryNotFoundException`, `CategoryValidationException`을 `gift.category.exception`으로 이동한다.

**이유**:

- product/member/order/auth/option 패키지는 예외를 하위 `exception` 패키지에서 관리하고 있다.
- category만 루트 패키지에 예외가 있으면 도메인 패키지 구조가 불규칙하다.
- 예외 클래스를 모아두면 category 도메인에서 발생 가능한 실패 유형을 한 곳에서 파악하기 쉽다.

**대안**:

- 루트 패키지 유지: 변경량은 적지만 다른 도메인과 구조가 맞지 않는다.
- `CategoryException` base class 추가: 장기적으로 가능하지만 이번 작업의 목적보다 범위가 커진다.

## 결정 2: 외부 응답 계약은 변경하지 않음

**결정**: status, error code, message를 변경하지 않는다.

**이유**:

- 이번 작업은 패키지 구조 리팩토링이다.
- API 사용자에게 보이는 응답이 바뀌면 구조 개선 이상의 영향이 생긴다.
- 기존 `CategoryControllerTest`, `GlobalExceptionHandlerTest`가 회귀 안전망 역할을 한다.

## 결정 3: 테스트는 기존 동작 유지 중심으로 검증

**결정**: 새 기능 테스트를 대량 추가하지 않고, import 변경 후 기존 category/global 테스트와 전체 테스트 통과를 완료 조건으로 삼는다.

**이유**:

- 기능 요구사항은 이미 030, 032 spec에서 검증하고 있다.
- 이번 작업의 핵심 위험은 컴파일/import 누락과 handler 매핑 깨짐이다.
