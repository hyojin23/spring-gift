# 구현 계획: Admin 예외 처리 분리 리팩토링

**Branch**: `038-admin-exception-handler-refactor`  
**Spec**: `specs/038-admin-exception-handler-refactor/spec.md`  
**작성일**: 2026-05-23

## 요약

`AdminProductController`와 `AdminMemberController` 내부의 `@ExceptionHandler`를 별도 `@ControllerAdvice` 클래스로 분리합니다. 기존 redirect 경로와 flash error 전달 방식은 유지합니다.

## 기술 컨텍스트

**언어/버전**: Java 21  
**프레임워크**: Spring Boot, Spring MVC, Thymeleaf  
**테스트**: JUnit 5, Spring MockMvc  
**대상 패키지**: `gift.product`, `gift.member`  
**제약**: admin 화면 응답 계약 유지  

## 리팩토링 범위

### 포함

- `AdminProductExceptionHandler` 추가
- `AdminMemberExceptionHandler` 추가
- 두 controller 내부 `@ExceptionHandler` 제거
- 불필요한 import 제거
- admin controller 테스트 실행
- 전체 테스트 실행

### 제외

- REST API `GlobalExceptionHandler` 변경
- flash attribute key 변경
- redirect 경로 변경
- admin service 예외 계층 변경
- 공통 helper/추상 advice 도입

## 구현 단계

1. 두 admin controller의 `@ExceptionHandler`를 확인한다.
2. product admin 전용 `@ControllerAdvice(assignableTypes = AdminProductController.class)`를 추가한다.
3. member admin 전용 `@ControllerAdvice(assignableTypes = AdminMemberController.class)`를 추가한다.
4. 기존 controller 내부 handler method를 제거한다.
5. 불필요한 import를 제거한다.
6. admin controller 테스트를 실행한다.
7. 전체 테스트를 실행한다.

## 검증 전략

- `AdminProductControllerTest`로 상품/카테고리 예외 redirect + flash error를 검증합니다.
- `AdminMemberControllerTest`로 회원/포인트 예외 redirect + flash error를 검증합니다.
- 전체 테스트로 REST API global handler와 다른 화면 흐름 영향을 확인합니다.

## 리스크와 대응

- **리스크**: advice 적용 범위가 너무 넓어 다른 controller 예외를 가로챌 수 있음  
  **대응**: `assignableTypes`로 대상 controller를 제한합니다.

- **리스크**: redirect 경로나 flash key가 바뀌어 화면 테스트가 실패할 수 있음  
  **대응**: 기존 테스트 기대값을 그대로 유지하고 회귀 검증합니다.

## 완료 조건

- admin controller 내부에 `@ExceptionHandler`가 없습니다.
- 기존 admin 예외 redirect/flash 동작이 유지됩니다.
- admin controller 테스트와 전체 테스트가 통과합니다.
