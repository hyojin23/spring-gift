# 구현 계획: Wish 상품 미존재 예외 분리 리팩토링

**Branch**: `036-wish-product-exception-refactor`  
**Spec**: `specs/036-wish-product-exception-refactor/spec.md`  
**작성일**: 2026-05-22

## 요약

`WishService.addWish()`에서 위시에 추가할 상품 조회 실패를 `WishNotFoundException`이 아니라 `WishProductNotFoundException`으로 표현합니다. global handler에는 404 `WISH.PRODUCT_NOT_FOUND` 매핑을 추가하고, 기존 위시 항목 미존재 응답은 그대로 유지합니다.

## 기술 컨텍스트

**언어/버전**: Java 21  
**프레임워크**: Spring Boot, Spring MVC, Spring Data JPA  
**테스트**: JUnit 5, AssertJ, Spring MockMvc  
**대상 패키지**: `gift.wish`, `gift.wish.exception`, `gift.global`  
**제약**: 기존 위시 미존재 응답 변경 금지  

## 리팩토링 범위

### 포함

- `WishProductNotFoundException` 추가
- `WishService.addWish()` 예외 타입 변경
- global handler 매핑 추가
- wish controller/global handler 테스트 보강
- 관련 spec task 완료 표시

### 제외

- product 도메인 예외 재사용
- wish 도메인 검증 강화
- 위시 중복 처리 정책 변경
- 인증 처리 변경
- DB 제약 조건 변경

## 구현 단계

1. `WishService.addWish()`의 상품 조회 실패 예외를 확인한다.
2. `WishProductNotFoundException`을 추가한다.
3. `WishService.addWish()`가 새 예외를 던지도록 변경한다.
4. global handler에 `WISH.PRODUCT_NOT_FOUND` 매핑을 추가한다.
5. `WishControllerTest`에 존재하지 않는 상품 위시 추가 실패 테스트를 추가한다.
6. `GlobalExceptionHandlerTest`에 새 예외 매핑 테스트를 추가한다.
7. 관련 테스트와 전체 테스트를 실행한다.

## 검증 전략

- `WishControllerTest`로 실제 API 응답 status/code/message를 검증합니다.
- `GlobalExceptionHandlerTest`로 예외 매핑을 직접 검증합니다.
- 기존 delete wish not found 테스트로 `WISH.NOT_FOUND` 유지 여부를 확인합니다.
- 전체 테스트로 회귀를 확인합니다.

## 리스크와 대응

- **리스크**: 기존 테스트가 `WISH.NOT_FOUND`를 기대할 수 있음  
  **대응**: 위시 삭제 미존재 테스트는 그대로 유지하고, 상품 미존재 추가 테스트만 새 code를 기대하도록 분리합니다.

- **리스크**: error code 증가로 클라이언트 처리 분기가 필요할 수 있음  
  **대응**: status는 동일한 404를 유지하고, code만 더 구체화합니다.

## 완료 조건

- 상품 미존재와 위시 미존재가 서로 다른 예외/code로 표현됩니다.
- 기존 위시 미존재 응답은 유지됩니다.
- 관련 테스트와 전체 테스트가 통과합니다.
