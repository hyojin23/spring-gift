# 구현 계획: Order 인증 예외 응답 일관화 리팩토링

**Branch**: `034-order-auth-exception-refactor`  
**Spec**: `specs/034-order-auth-exception-refactor/spec.md`  
**작성일**: 2026-05-22

## 요약

`OrderController`의 인증 실패 처리를 직접 빈 401 응답 반환에서 `AuthenticationException` 발생 방식으로 변경합니다. 이를 통해 global handler가 기존 wish API와 동일한 401 `ErrorResponse`를 반환하게 합니다.

## 기술 컨텍스트

**언어/버전**: Java 21  
**프레임워크**: Spring Boot, Spring MVC  
**테스트**: JUnit 5, Spring MockMvc  
**대상 패키지**: `gift.order`, `gift.wish.exception`, `gift.global`  
**제약**: `AuthenticationResolver.extractMember()`의 null 반환 정책 유지  

## 리팩토링 범위

### 포함

- `OrderController` 인증 실패 시 `AuthenticationException` 발생
- 중복 인증 추출 로직을 private method로 정리
- `ResponseEntity<?>` 반환 타입 개선
- `OrderControllerTest` 인증 실패 응답 body 검증 강화
- 관련 spec task 완료 표시

### 제외

- `AuthenticationResolver` API 변경
- wish/order 공통 인증 resolver 도입
- JWT 예외 처리 정책 변경
- 주문 service 로직 변경

## 구현 단계

1. `OrderController`의 인증 실패 직접 응답 반환 위치를 확인한다.
2. `WishController`의 인증 실패 처리 흐름을 참고한다.
3. `OrderController`에 인증 member 추출 private method를 추가한다.
4. 인증 실패 시 `AuthenticationException`을 던지도록 변경한다.
5. controller 반환 타입을 실제 응답 타입에 맞게 정리한다.
6. `OrderControllerTest`에서 인증 실패 응답 body를 검증한다.
7. 관련 테스트와 전체 테스트를 실행한다.

## 검증 전략

- `OrderControllerTest`로 인증 실패 status/code/message를 검증합니다.
- 기존 주문 생성/조회 성공 테스트로 정상 흐름을 검증합니다.
- 전체 테스트로 global handler와 다른 API 회귀를 확인합니다.

## 리스크와 대응

- **리스크**: Authorization header가 required 상태라 header 누락 시 Spring 기본 400/500 흐름으로 갈 수 있음  
  **대응**: header를 `required = false`로 맞추고 controller 내부에서 인증 실패 예외로 변환합니다.

- **리스크**: 테스트 기대 body가 기존 빈 401에서 변경됨  
  **대응**: 이번 spec의 목적이 응답 body 일관화이므로 테스트를 새 계약에 맞게 갱신합니다.

## 완료 조건

- order 인증 실패 응답이 `AUTH.UNAUTHORIZED` body를 포함합니다.
- `OrderController`에 빈 401 직접 반환이 남아 있지 않습니다.
- 관련 테스트와 전체 테스트가 통과합니다.
