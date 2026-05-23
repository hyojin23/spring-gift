# Research: Product 유스케이스 서비스 공통화 리팩토링

## 결정 1: 생성/수정 유스케이스부터 공통화

**결정**: 1차 작업에서는 상품 생성/수정 저장 흐름만 공통화한다.

**이유**:

- `ProductService`와 `AdminProductService`에서 가장 직접적으로 중복되는 부분이다.
- delete 흐름은 단순하지만 예외 정책과 사용처를 더 확인할 필요가 있다.
- admin service 제거까지 한 번에 진행하면 controller/model 영향 범위가 커진다.

## 결정 2: 상품명 검증 정책은 경로별로 유지

**결정**: API는 기존 `ProductNameValidator.validate(name)`을 사용하고, admin은 기존 `ProductNameValidator.validate(name, true)`를 유지한다.

**이유**:

- API는 `"카카오"` 포함 상품명을 제한한다.
- 관리자 화면은 기존 테스트에서 `"카카오"` 포함 상품명을 허용한다.
- 저장 유스케이스를 공통화해도 진입 경로별 정책 차이는 유지되어야 한다.

## 결정 3: 관리자 service는 당장 제거하지 않음

**결정**: `AdminProductService`는 관리자 화면 보조 service로 유지한다.

**이유**:

- 관리자 화면은 상품 목록, 카테고리 목록, form validation 오류 복구 등 API와 다른 관심사가 있다.
- controller가 바로 `ProductService`를 사용하도록 바꾸면 화면 모델 책임이 controller로 다시 커질 수 있다.
- 점진적 리팩토링이 안전하다.

## 결정 4: 예외 타입 혼합을 경계

**결정**: 공통 유스케이스를 도입하더라도 API 예외와 admin 예외의 외부 응답 계약은 유지해야 한다.

**이유**:

- API는 JSON `ErrorResponse`를 반환한다.
- admin 화면은 redirect + flash error를 반환한다.
- 같은 내부 로직을 사용하더라도 외부 경로별 예외 응답은 다르다.
