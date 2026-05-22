# Research: 인증 Member 추출 공통화 리팩토링

## 결정 1: 새 컴포넌트 `AuthenticatedMemberResolver` 추가

**결정**: `AuthenticationResolver`를 직접 변경하지 않고, 인증 필수 API 전용 `AuthenticatedMemberResolver`를 추가한다.

**이유**:

- `AuthenticationResolver.extractMember()`는 현재 null 반환 정책을 가진다.
- null 반환은 인증 선택 흐름이나 내부 판단이 필요한 곳에서 재사용될 수 있다.
- 인증 필수 API의 "member가 없으면 예외" 정책은 별도 컴포넌트가 더 명확하다.

**대안**:

- `AuthenticationResolver.extractMember()`가 바로 예외를 던지도록 변경: 간단하지만 기존 null 반환 계약이 깨진다.
- controller마다 private method 유지: 변경량은 없지만 중복과 정책 분산이 남는다.

## 결정 2: 기존 `AuthenticationException` 재사용

**결정**: 인증 실패 예외는 기존 `gift.wish.exception.AuthenticationException`을 재사용한다.

**이유**:

- global handler가 이미 이 예외를 401 `AUTH.UNAUTHORIZED`로 처리한다.
- 이번 작업의 목적은 member 추출 공통화이며 예외 패키지 재설계가 아니다.
- 응답 계약 변경 없이 리팩토링할 수 있다.

**후속 후보**:

- `AuthenticationException`을 `gift.auth.exception`으로 이동해 auth 도메인 예외로 정리할 수 있다.

## 결정 3: Controller argument resolver는 도입하지 않음

**결정**: Spring MVC `HandlerMethodArgumentResolver`나 custom annotation은 이번 작업에서 도입하지 않는다.

**이유**:

- 현재 필요한 것은 두 controller의 중복 제거다.
- argument resolver는 더 편리하지만 테스트/설정 범위가 커진다.
- 작은 컴포넌트 추출만으로 충분한 개선 효과가 있다.
