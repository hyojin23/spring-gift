# Research: Admin 예외 처리 분리 리팩토링

## 결정 1: controller별 `@ControllerAdvice`를 둔다

**결정**: `AdminProductExceptionHandler`, `AdminMemberExceptionHandler`를 각각 추가한다.

**이유**:

- redirect 경로가 `/admin/products`, `/admin/members`로 다르다.
- 예외 타입도 `AdminProductException`, `MemberException`으로 다르다.
- 각 advice를 controller에 한정하면 적용 범위가 명확하다.

**대안**:

- 하나의 공통 advice에서 두 예외를 처리: 가능하지만 redirect 경로 분기 로직이 생긴다.
- 공통 helper를 만들어 두 advice가 호출: 현재 중복이 작아 과한 추상화일 수 있다.

## 결정 2: REST API global handler와 분리

**결정**: `GlobalExceptionHandler`는 수정하지 않는다.

**이유**:

- REST API는 JSON body를 반환한다.
- admin 화면은 redirect + flash attribute를 반환한다.
- 서로 다른 응답 정책을 한 클래스에 섞으면 의도가 흐려진다.

## 결정 3: 기존 테스트 기대값 유지

**결정**: admin controller 테스트의 redirect URL과 flash error 기대값은 변경하지 않는다.

**이유**:

- 이번 작업은 위치 이동 리팩토링이다.
- 사용자에게 보이는 화면 동작은 변경되면 안 된다.
