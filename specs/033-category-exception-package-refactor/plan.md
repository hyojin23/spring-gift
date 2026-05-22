# 구현 계획: Category 예외 패키지 정리 리팩토링

**Branch**: `033-category-exception-package-refactor`  
**Spec**: `specs/033-category-exception-package-refactor/spec.md`  
**작성일**: 2026-05-22

## 요약

Category 예외 클래스를 `gift.category` 루트 패키지에서 `gift.category.exception` 하위 패키지로 이동합니다. 외부 API 응답과 비즈니스 동작은 변경하지 않고, import와 테스트만 새 패키지 구조에 맞게 정리합니다.

## 기술 컨텍스트

**언어/버전**: Java 21  
**프레임워크**: Spring Boot, Spring MVC, Spring Data JPA  
**테스트**: JUnit 5, AssertJ, Spring MVC Test  
**대상 패키지**: `gift.category`, `gift.category.exception`, `gift.global`  
**제약**: API 응답 status/code/message 변경 금지  

## 리팩토링 범위

### 포함

- `CategoryNotFoundException` 패키지 이동
- `CategoryValidationException` 패키지 이동
- `CategoryService`, `Category`, `GlobalExceptionHandler`, 테스트 import 정리
- category/global 관련 테스트 실행

### 제외

- 예외 메시지 변경
- error code 변경
- category service 로직 변경
- category request validation 변경
- 공통 category base exception 추가

## 구현 단계

1. 현재 category 예외 사용처를 검색한다.
2. `gift.category.exception` 패키지를 생성한다.
3. category 예외 클래스의 package 선언과 파일 위치를 변경한다.
4. 모든 import를 새 패키지로 정리한다.
5. category/global handler 테스트를 실행한다.
6. 전체 테스트를 실행한다.

## 검증 전략

- 단위 테스트: `CategoryTest`
- MVC/service 흐름 테스트: `CategoryControllerTest`
- 전역 예외 처리 테스트: `GlobalExceptionHandlerTest`
- 회귀 검증: 전체 테스트

## 리스크와 대응

- **리스크**: import 누락으로 컴파일 실패  
  **대응**: 전체 테스트로 compile/test를 함께 검증합니다.

- **리스크**: 기존 handler가 이동된 예외를 처리하지 못함  
  **대응**: `GlobalExceptionHandlerTest`에서 status/code/message를 확인합니다.

## 완료 조건

- category 예외가 `gift.category.exception` 하위에 위치합니다.
- 기존 category 예외 응답이 유지됩니다.
- 관련 테스트와 전체 테스트가 통과합니다.
