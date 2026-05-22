# 구현 계획: 인증 Member 추출 공통화 리팩토링

**Branch**: `035-authenticated-member-resolver-refactor`  
**Spec**: `specs/035-authenticated-member-resolver-refactor/spec.md`  
**작성일**: 2026-05-22

## 요약

`OrderController`와 `WishController`에 반복된 인증 member 추출 및 null 검사 로직을 `AuthenticatedMemberResolver`로 분리합니다. 기존 `AuthenticationResolver`는 토큰 파싱/회원 조회를 담당하고, 새 resolver는 인증 필수 API에서 member가 없을 때 `AuthenticationException`을 던지는 정책을 담당합니다.

## 기술 컨텍스트

**언어/버전**: Java 21  
**프레임워크**: Spring Boot, Spring MVC  
**테스트**: JUnit 5, Mockito 또는 Spring Boot Test, MockMvc  
**대상 패키지**: `gift.auth`, `gift.order`, `gift.wish`  
**제약**: API 응답 status/code/message 변경 금지  

## 리팩토링 범위

### 포함

- `AuthenticatedMemberResolver` 추가
- resolver 단위 테스트 추가
- `OrderController` 주입 대상 변경
- `WishController` 주입 대상 변경
- 중복 private `extractMember()` 제거
- order/wish controller 테스트 실행

### 제외

- `AuthenticationResolver.extractMember()` 반환 타입 변경
- `AuthenticationException` 패키지 이동
- JWT 파싱 정책 변경
- 인증 argument resolver 도입
- Spring Security 도입

## 구현 단계

1. order/wish controller의 중복 인증 추출 코드를 확인한다.
2. `AuthenticatedMemberResolver`를 `gift.auth` 패키지에 추가한다.
3. valid/null/invalid header에 대한 resolver 테스트를 작성한다.
4. `OrderController`가 새 resolver를 사용하도록 변경한다.
5. `WishController`가 새 resolver를 사용하도록 변경한다.
6. controller의 private 인증 추출 method를 제거한다.
7. 관련 테스트와 전체 테스트를 실행한다.

## 검증 전략

- `AuthenticatedMemberResolverTest`로 공통 resolver 정책을 검증합니다.
- `OrderControllerTest`로 order 인증 실패 응답 회귀를 검증합니다.
- `WishControllerTest`로 wish 인증 실패 응답 회귀를 검증합니다.
- 전체 테스트로 다른 auth 흐름 영향을 확인합니다.

## 리스크와 대응

- **리스크**: `AuthenticationException`이 wish 패키지에 있어 auth 컴포넌트에서 참조가 어색할 수 있음  
  **대응**: 이번 작업에서는 기존 예외를 재사용하고, 예외 패키지 이동은 별도 spec으로 분리합니다.

- **리스크**: controller 생성자 시그니처 변경으로 테스트 context 실패  
  **대응**: Spring Boot 테스트와 전체 테스트로 Bean wiring을 확인합니다.

## 완료 조건

- 인증 필수 controller가 공통 resolver를 사용합니다.
- order/wish controller의 중복 private 인증 추출 method가 제거됩니다.
- 인증 실패 응답이 기존과 동일합니다.
- 관련 테스트와 전체 테스트가 통과합니다.
