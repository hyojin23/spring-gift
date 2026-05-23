# 구현 계획: 인증 Member Argument Resolver 리팩토링

**Branch**: `038-authenticated-member-argument-resolver`  
**Spec**: `specs/038-authenticated-member-argument-resolver/spec.md`  
**작성일**: 2026-05-23

## 요약

`WishController`와 `OrderController`에서 반복되는 `Authorization` header 수신 및 `AuthenticatedMemberResolver.resolve()` 호출을 Spring MVC `HandlerMethodArgumentResolver`로 이동합니다. 컨트롤러는 `@Authenticated Member member` 파라미터를 선언하고, MVC argument resolver가 요청 header에서 인증 회원을 찾아 주입합니다.

## 기술 컨텍스트

**언어/버전**: Java 21  
**프레임워크**: Spring Boot, Spring MVC, Spring Data JPA  
**테스트**: JUnit 5, Mockito, Spring Boot Test, MockMvc  
**대상 패키지**: `gift.auth`, `gift.order`, `gift.wish`  
**저장소 변경**: 없음  
**API 계약 변경**: 없음  
**제약**: 인증 실패 응답은 기존 401 `AUTH.UNAUTHORIZED`를 유지해야 함

## Constitution Check

- 기존 도메인/패키지 구조를 유지합니다.
- 리팩토링 범위를 인증 파라미터 주입으로 제한합니다.
- 외부 API 요청/응답 스키마를 변경하지 않습니다.
- 테스트로 기존 order/wish 인증 성공/실패 흐름을 검증합니다.

## 리팩토링 범위

### 포함

- `@Authenticated` parameter annotation 추가
- `AuthenticatedMemberArgumentResolver` 추가
- MVC 설정 클래스에서 argument resolver 등록
- `WishController`의 직접 인증 header 처리 제거
- `OrderController`의 직접 인증 header 처리 제거
- resolver 단위 테스트와 controller 회귀 테스트 보강

### 제외

- Spring Security 도입
- JWT 파싱 정책 변경
- `AuthenticationResolver.extractMember()` 반환 타입 변경
- API endpoint, request body, response body 변경
- 주문/위시 서비스 비즈니스 로직 변경

## 구현 단계

1. `gift.auth.Authenticated` 애노테이션을 추가한다.
2. `gift.auth.AuthenticatedMemberArgumentResolver`를 추가한다.
3. resolver가 `@Authenticated Member`만 지원하도록 구현한다.
4. resolver가 `Authorization` header를 읽고 기존 `AuthenticatedMemberResolver`에 위임하도록 구현한다.
5. `WebMvcConfigurer` 구현체에서 argument resolver를 등록한다.
6. `OrderController` 메서드 파라미터를 `@Authenticated Member member`로 변경한다.
7. `WishController` 메서드 파라미터를 `@Authenticated Member member`로 변경한다.
8. resolver 단위 테스트를 추가한다.
9. order/wish controller 테스트가 기존 응답 계약을 유지하는지 확인한다.

## 검증 전략

- `AuthenticatedMemberArgumentResolverTest`로 지원 파라미터, 비지원 파라미터, 성공/실패 해석을 검증합니다.
- `OrderControllerTest`로 주문 목록/생성 API의 인증 성공 및 실패 응답을 검증합니다.
- `WishControllerTest`로 위시 목록/추가/삭제 API의 인증 성공 및 실패 응답을 검증합니다.
- 전체 테스트로 다른 인증 흐름과 Bean wiring 영향을 확인합니다.

## 리스크와 대응

- **리스크**: `@Authenticated` 없는 `Member` 파라미터까지 resolver가 처리하면 의도하지 않은 주입이 발생할 수 있음  
  **대응**: `supportsParameter()`에서 애노테이션과 타입을 모두 검사합니다.

- **리스크**: MVC 설정 누락으로 컨트롤러 호출 시 파라미터 해석 실패  
  **대응**: controller 테스트를 Spring context 기반으로 실행해 등록 여부를 검증합니다.

- **리스크**: 인증 실패 예외의 패키지가 `gift.wish.exception`에 있어 auth 패키지에서 어색함  
  **대응**: 이번 작업에서는 기존 응답 계약 유지를 우선하고, 예외 패키지 이동은 별도 리팩토링으로 분리합니다.

## 완료 조건

- `WishController`와 `OrderController`에 직접 `Authorization` header 파라미터가 남아 있지 않습니다.
- 인증 필수 API는 `@Authenticated Member` 파라미터를 사용합니다.
- 인증 실패 응답은 기존과 동일한 401 `AUTH.UNAUTHORIZED`입니다.
- 관련 테스트와 전체 테스트가 통과합니다.
