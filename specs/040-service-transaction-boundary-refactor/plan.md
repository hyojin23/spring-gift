# 구현 계획: Service 트랜잭션 경계 일관화 리팩토링

**Branch**: `040-service-transaction-boundary-refactor`  
**Spec**: `specs/040-service-transaction-boundary-refactor/spec.md`  
**작성일**: 2026-05-23

## 요약

DB 접근 service에 트랜잭션 경계를 명시합니다. 클래스 레벨 기본값은 `@Transactional(readOnly = true)`로 두고, 생성/수정/삭제 메서드는 `@Transactional`로 override하는 패턴을 우선 적용합니다.

## 기술 컨텍스트

**언어/버전**: Java 21  
**프레임워크**: Spring Boot, Spring Data JPA, Spring Transaction  
**테스트**: JUnit 5, Spring MockMvc  
**대상 패키지**: `gift.category`, `gift.product`, `gift.option`, `gift.wish`, `gift.member`, `gift.order`  
**제약**: 기능 동작과 응답 계약 변경 금지  

## 리팩토링 범위

### 포함

- DB 읽기/쓰기 service 트랜잭션 annotation 추가
- `CategoryService` 클래스 레벨 트랜잭션 readOnly 기본값으로 정리
- 쓰기 메서드 `@Transactional` 명시
- 기존 `OrderService` 트랜잭션 정책 확인 및 유지
- 전체 테스트 실행

### 제외

- controller/repository 트랜잭션 변경
- 외부 API client service 트랜잭션 추가
- 비즈니스 로직 변경
- 예외 타입/응답 code 변경
- DB schema 변경

## 구현 단계

1. 모든 `*Service`의 DB 접근 여부와 메서드 성격을 확인한다.
2. DB 접근 service 클래스에 `@Transactional(readOnly = true)`를 추가한다.
3. 생성/수정/삭제/포인트 변경/주문 생성 메서드에 `@Transactional`을 추가한다.
4. 기존 method-level annotation이 있는 경우 중복/충돌 없이 정리한다.
5. 외부 API client service는 제외한다.
6. 전체 테스트를 실행한다.

## 적용 대상 후보

- `CategoryService`
- `ProductService`
- `ProductUseCaseService`
- `AdminProductService`
- `OptionService`
- `WishService`
- `MemberService`
- `AdminMemberService`
- `OrderService`

## 제외 대상 후보

- `KakaoAuthService`
- `OrderNotificationService`

## 검증 전략

- 우선 전체 테스트로 컴파일과 동작 회귀를 확인합니다.
- 실패 시 해당 도메인 테스트를 좁혀 원인을 확인합니다.
- HTTP 응답/redirect/flash 기대값이 바뀌지 않는지 기존 테스트로 확인합니다.

## 리스크와 대응

- **리스크**: 클래스 레벨 readOnly로 인해 쓰기 메서드에 annotation을 빠뜨리면 저장이 의도대로 동작하지 않을 수 있음  
  **대응**: 모든 쓰기 메서드를 목록화하고 `@Transactional`을 명시합니다.

- **리스크**: 외부 API 호출이 DB 트랜잭션 안에 묶일 수 있음  
  **대응**: 외부 API client service는 제외하고, 주문 서비스의 기존 구조를 신중히 유지합니다.

## 완료 조건

- DB 접근 service의 read/write 트랜잭션 의도가 명확합니다.
- 전체 테스트가 통과합니다.
