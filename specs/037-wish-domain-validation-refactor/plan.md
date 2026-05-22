# 구현 계획: Wish 도메인 검증 강화 리팩토링

**Branch**: `037-wish-domain-validation-refactor`  
**Spec**: `specs/037-wish-domain-validation-refactor/spec.md`  
**작성일**: 2026-05-23

## 요약

`Wish` 도메인 생성 시 memberId와 product가 null인 잘못된 상태를 막습니다. 검증 실패는 `WishValidationException`으로 표현하고, global handler에서 400 `WISH.INVALID` 응답으로 변환합니다.

## 기술 컨텍스트

**언어/버전**: Java 21  
**프레임워크**: Spring Boot, JPA, Spring MVC  
**테스트**: JUnit 5, AssertJ, Mockito, MockMvc  
**대상 패키지**: `gift.wish`, `gift.wish.exception`, `gift.global`  
**제약**: JPA 기본 생성자 유지, 기존 API 응답 회귀 방지  

## 리팩토링 범위

### 포함

- `WishValidationException` 추가
- `Wish` 생성자 null 검증 추가
- global handler 매핑 추가
- `WishTest` 추가
- `WishServiceTest` fixture 보정
- 관련 테스트와 전체 테스트 실행

### 제외

- memberId 양수 검증
- memberId 실제 존재 여부 조회
- productId 중복 정책 변경
- DB not null 제약 추가
- WishResponse 구조 변경

## 구현 단계

1. `Wish` 생성자와 기존 테스트 fixture를 확인한다.
2. `WishTest`를 추가해 정상 생성과 null 검증 실패를 작성한다.
3. `WishValidationException`을 추가한다.
4. `Wish` 생성자에서 memberId/product null을 검증한다.
5. global handler에 `WISH.INVALID` 매핑을 추가한다.
6. `WishServiceTest`의 product null fixture를 유효한 product로 보정한다.
7. 관련 테스트와 전체 테스트를 실행한다.

## 검증 전략

- `WishTest`로 도메인 생성 불변 조건을 검증합니다.
- `GlobalExceptionHandlerTest`로 `WISH.INVALID` 매핑을 검증합니다.
- `WishServiceTest`로 기존 service 예외 흐름을 검증합니다.
- `WishControllerTest`로 API 회귀를 검증합니다.
- 전체 테스트로 다른 도메인 영향 여부를 확인합니다.

## 리스크와 대응

- **리스크**: 기존 테스트가 `new Wish(memberId, null)` fixture를 사용해 실패할 수 있음  
  **대응**: fixture를 실제 product 객체로 보정합니다.

- **리스크**: JPA proxy나 fixture에서 product가 준비되지 않은 경우 생성 실패  
  **대응**: repository 조회를 통해 product를 확보한 뒤 Wish를 생성하는 기존 service 흐름은 유지됩니다.

## 완료 조건

- Wish가 memberId/product null로 생성되지 않습니다.
- 검증 실패가 `WISH.INVALID` 응답으로 변환됩니다.
- 관련 테스트와 전체 테스트가 통과합니다.
