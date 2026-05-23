# 구현 계획: Product 유스케이스 서비스 공통화 리팩토링

**Branch**: `039-product-usecase-service-refactor`  
**Spec**: `specs/039-product-usecase-service-refactor/spec.md`  
**작성일**: 2026-05-23

## 요약

`ProductService`와 `AdminProductService`에 중복된 상품 생성/수정 저장 흐름을 공통 유스케이스로 모읍니다. API와 관리자 화면의 응답 형식 및 상품명 검증 정책 차이는 유지합니다.

## 기술 컨텍스트

**언어/버전**: Java 21  
**프레임워크**: Spring Boot, Spring MVC, Spring Data JPA  
**테스트**: JUnit 5, AssertJ, Spring MockMvc  
**대상 패키지**: `gift.product`, `gift.category`  
**제약**: API 응답과 관리자 redirect/flash 동작 변경 금지  

## 리팩토링 범위

### 포함

- 상품 생성 공통 유스케이스 추출
- 상품 수정 공통 유스케이스 추출
- API service가 공통 유스케이스를 사용하도록 정리
- admin service가 공통 유스케이스를 사용하도록 정리
- 기존 상품명 검증 정책 유지
- product/admin product 테스트 실행

### 제외

- admin service 완전 제거
- delete 흐름 공통화
- ProductRequest 구조 변경
- 관리자 화면 model 구조 변경
- 예외 클래스 패키지/계층 변경
- DB schema 변경

## 구현 방향

안전한 1차 방향은 `ProductService`를 핵심 상품 유스케이스 소유자로 두고, 관리자 서비스가 이를 재사용하는 방식입니다.

```text
ProductService
  - createProduct(...)
  - updateProduct(...)
  - API response 변환
  - 공통 저장 유스케이스 private/helper method

AdminProductService
  - getProducts()
  - getCategories()
  - getProduct()
  - validateName(name, allowKakao=true)
  - create/update는 ProductService의 공통 흐름 재사용
```

필요하면 command record를 추가할 수 있습니다.

```java
record ProductCommand(String name, int price, String imageUrl, Long categoryId) {
}
```

## 구현 단계

1. `ProductService`와 `AdminProductService`의 생성/수정 중복을 확인한다.
2. 상품 생성/수정 입력을 표현할 내부 command 또는 메서드 시그니처를 정한다.
3. `ProductService`에 공통 생성/수정 유스케이스를 만든다.
4. API 메서드는 기존처럼 검증 후 `ProductResponse`로 변환한다.
5. 관리자 메서드는 기존 관리자 검증 정책을 유지한 뒤 공통 유스케이스를 호출한다.
6. 관리자 미존재 예외 흐름이 깨지지 않는지 확인한다.
7. 관련 테스트와 전체 테스트를 실행한다.

## 검증 전략

- `ProductControllerTest`로 API 응답 회귀를 검증합니다.
- `ProductServiceTest`로 API 상품명/카테고리 예외 흐름을 검증합니다.
- `AdminProductControllerTest`로 관리자 redirect/flash/model 흐름을 검증합니다.
- 전체 테스트로 product 변경이 option/order/wish와 충돌하지 않는지 확인합니다.

## 리스크와 대응

- **리스크**: API와 관리자 예외 타입이 섞일 수 있음  
  **대응**: 관리자 진입점에서는 기존 admin 예외 변환 또는 기존 admin 조회 메서드를 유지합니다.

- **리스크**: 상품명 검증 정책 차이가 사라질 수 있음  
  **대응**: API는 `allowKakao=false`, admin은 `allowKakao=true` 테스트를 유지합니다.

- **리스크**: 한 번에 service 구조를 크게 바꾸면 회귀 범위가 커짐  
  **대응**: 생성/수정 유스케이스 공통화만 1차 범위로 제한합니다.

## 완료 조건

- 상품 생성/수정 저장 흐름의 중복이 줄어듭니다.
- API와 관리자 상품명 정책 차이는 유지됩니다.
- product/admin product 관련 테스트와 전체 테스트가 통과합니다.
